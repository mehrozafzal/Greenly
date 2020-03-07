package greenely.greenely.home.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.VisibleForTesting
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nex3z.notificationbadge.NotificationBadge
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.BuildConfig
import greenely.greenely.R
import greenely.greenely.databinding.*
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.guidance.ui.GuidanceFragment
import greenely.greenely.history.views.HolderModel
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.ui.events.EventHandler
import greenely.greenely.home.ui.events.EventHandlerFactory
import greenely.greenely.home.ui.events.RouteChangeHandler
import greenely.greenely.home.ui.historicalcomparison.HistoricalComparisonChartSetupFactory
import greenely.greenely.home.ui.latestcomparison.LatestComparisonChartSetupFactory
import greenely.greenely.home.ui.viewholders.HistoricalComparisonViewHolder
import greenely.greenely.home.ui.viewholders.LatestComparisonViewHolder
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.router.Route
import greenely.greenely.models.Resource
import greenely.greenely.profile.json.SaveProfileResponse
import greenely.greenely.profile.model.Account
import greenely.greenely.settings.ui.SettingsFragment
import greenely.greenely.splash.ui.SplashActivity
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.welcome.ui.WelcomeActivity
import io.intercom.android.sdk.Intercom
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject

/**
 * Fragment for displaying the home view.
 */
class HomeFragment : androidx.fragment.app.Fragment(), HasSnackbarView, HomeHelper {
    override val snackbarView: View
        get() = binding.container

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var errorHandler: HomeErrorHandler

    @Inject
    internal lateinit var routeChangeHandler: RouteChangeHandler

    @Inject
    lateinit var latestComparisonSetupFactory: LatestComparisonChartSetupFactory

    @Inject
    internal lateinit var historicalComparisonSetupFactory: HistoricalComparisonChartSetupFactory

    @VisibleForTesting
    internal lateinit var eventHandler: EventHandler

    @Inject
    internal lateinit var eventHandlerFactory: EventHandlerFactory

    @Inject
    lateinit var userStore: UserStore


    private lateinit var binding: HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel

    private lateinit var activityHandler: IMainAcitvityListener

    private lateinit var homeModel: HomeModel


    private val adapter by lazy {
        setUpAdapter()
    }

    private val activityCallBack: IMainAcitvityListener by lazy {
        activity as IMainAcitvityListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityHandler = activity as IMainAcitvityListener

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(HomeViewModel::class.java)

        binding.toolbar.setNavigationIcon(R.drawable.settings_icon)

        activity?.let {
            val bottomNavigationView: BottomNavigationView = it.findViewById(R.id.bottomNavigation)
            bottomNavigationView.visibility = View.VISIBLE
        }

        binding.viewModel = viewModel
        binding.toolbar.inflateMenu(R.menu.home_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.intercom -> {
                    Intercom.client().displayMessenger()
                    true
                }
                R.id.settings -> {
                    fragmentManager?.beginTransaction()
                            ?.addToBackStack(SettingsFragment::class.java.name)
                            ?.replace(R.id.content, SettingsFragment())
                            ?.commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            createSwitchProfileDialog()
        }

        if (BuildConfig.DEBUG) {
            when {
                RetrofitUrlManager.getInstance().globalDomain.toString().contains("api2") -> binding.toolbar.title = ""
                RetrofitUrlManager.getInstance().globalDomain.toString().contains("stagingapi") -> binding.toolbar.title = "Hem (Staging)"
                RetrofitUrlManager.getInstance().globalDomain.toString().contains("a15d4bd9") -> binding.toolbar.title = "Hem (Mock)"
            }
        }
        prepareNotificationBadge()

        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })
        viewModel.errors.observe(this, Observer { it?.let { errorHandler.handleError(it) } })

        viewModel.routeChangeEvent.observe(this, Observer { it?.let { routeChangeHandler.handleEvent(it) } })

        binding.ultraViewpager.layoutManager = LinearLayoutManager(context)

        adapter.setItems(mutableListOf<HolderModel>())
        binding.ultraViewpager.adapter = adapter

        viewModel.getHomeModel().observe(this, Observer {
            it?.let {
                when (it) {
                    is Resource.Success -> {
                        homeModel = it.value
                        binding.homeModel = it.value
                        viewModel.computeState(it.value)
                        adapter.setItems(createGraphList(it.value))
                        trackScreen(it.value.resolution)
                        bindGuidanceFragment()
                    }
                }
            }
        })

        viewModel.facilitiesResponse.observe(this, Observer {
            it?.let {
                when (it) {
                    is Resource.Success -> {
                        if (it.value.street != null) {
                            updateUserLocation(it.value.street)
                        } else {
                            updateUserLocation(it.value.parameters?.facilityType.toString())
                        }
                    }
                }
            }
        })

        binding.timeResolution.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != viewModel.resolution.get()) {
                viewModel.getDataForResolution(checkedId)
                trackChangeEvent("changed_comparison_resolution")
            }

        }

        binding.snackbarContainer.snackbarAction.setOnClickListener {
            viewModel.onRetailPromoSnackBarUpgradeClick()
            trackRetailSnackBarEvent("clicked_snackbar_retail_upgrade")
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.refereshRetailState()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun bindGuidanceFragment() {
        fragmentManager?.beginTransaction()?.replace(R.id.home_guidanceContainer, GuidanceFragment())?.commit()
    }

    private fun trackScreen(resolution: Int?) {
        when (resolution) {
            R.id.days -> tracker.trackScreen("Home Comparison Day")
            R.id.weeks -> tracker.trackScreen("Home Comparison Week")
            R.id.months -> tracker.trackScreen("Home Comparison Month")
        }
    }

    private fun trackChangeEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Home",
                "Home"
        ))
    }

    private fun trackRetailSnackBarEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Home",
                "Retail promotiona"
        ))
    }

    private fun trackPoa(label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                "poa_started",
                "POA",
                label
        ))
    }

    private fun trackCompeteFriend() {
        tracker.track(TrackerFactory().trackingEvent(
                "clicked_compete_shortcut_from_home",
                "Home"
        ))
    }

    private fun prepareNotificationBadge() {
        val menuItem: MenuItem = binding.toolbar.menu.findItem(R.id.intercom)
        menuItem.actionView.setOnClickListener { Intercom.client().displayMessenger() }
        val notificationBadge = menuItem.actionView.findViewById(R.id.badge) as NotificationBadge
        notificationBadge.setNumber(Intercom.client().unreadConversationCount, true)
        Intercom.client().addUnreadConversationCountListener {
            notificationBadge.setNumber(Intercom.client().unreadConversationCount, true)
        }
    }

    private fun setUpAdapter(): OnlyAdapter {
        return OnlyAdapter.Builder()
                .typeFactory {
                    when (it) {
                        is HolderModel.LatestComparisonHolder -> R.layout.latest_comparison_chart
                        else -> R.layout.historical_comparison_fragment
                    }
                }
                .viewHolderFactory { parent, type ->
                    when (type) {
                        R.layout.latest_comparison_chart -> LatestComparisonViewHolder(LatestComparisonChartBinding.inflate(
                                LayoutInflater.from(context), parent, false), latestComparisonSetupFactory)
                                .apply {

                                    binding.waitingContainerNoFriends.btnCompeteFriendScreen.setOnClickListener {
                                        trackCompeteFriend()
                                        activityCallBack.routeTo(Route.COMPETE_FRIEND)
                                    }

                                    binding.poaSignupContainer.btnFetchData.setOnClickListener {
                                        trackPoa(getString(R.string.first_attempt))
                                        viewModel.onClickFetchData()
                                    }

                                    binding.errorStateContainer.btnFetchData.setOnClickListener {
                                        trackPoa(getString(R.string.repeated_attempt))
                                        viewModel.onClickFetchData()
                                    }


                                }

                        else -> HistoricalComparisonViewHolder(HistoricalComparisonFragmentBinding.inflate(LayoutInflater.from(context), parent, false), historicalComparisonSetupFactory)
                    }
                }.build()
    }

    private fun createGraphList(homeModel: HomeModel): List<HolderModel> {
        val list = mutableListOf<HolderModel>()
        homeModel.latestComparisonModel?.let { list.add(HolderModel.LatestComparisonHolder(homeModel, it)) }
        homeModel.historicalComparisonModel?.let { list.add(HolderModel.HistoricalComparisonHolder(homeModel, it)) }
        return list
    }

    private fun createSwitchProfileDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val binding: DialogSwitchProfileBinding = DialogSwitchProfileBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        val accountsList: MutableList<Account> = ArrayList(userStore.getAccountsMapFromPreference().values)
        if (accountsList.isNotEmpty()) {
            for (account in accountsList) {
                if (account.userID == userStore.userId) {
                    binding.dialogSwitchProfileLoggedInUserName.text = account.name
                    binding.dialogSwitchProfileLoggedInUserEmail.text = account.email
                    accountsList.remove(account)
                    break
                }
            }
        }
        val accountAdapter = AccountAdapter(context!!, accountsList, this)
        binding.dialogSwitchProfileRv.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        binding.dialogSwitchProfileRv.adapter = accountAdapter
        binding.dialogSwitchProfileCloseBtn.setOnClickListener {
            dialog.dismiss()
        }
        binding.dialogSwitchProfileRvFooter.itemAccountFooterContainer.setOnClickListener {
            addAccount()
        }
        dialog.show()
    }

    override fun addAccount() {
        startActivity(Intent(context, SwitchProfileActivity::class.java))
//        activity?.finish()
    }

    override fun switchAccount(userID: Int) {
        updateToken(userID)
        startActivity(Intent(context, SplashActivity::class.java))
        activity?.finish()
    }

    private fun updateToken(userID: Int) {
        val accountMap = userStore.getAccountsMapFromPreference()
        for ((key, value) in accountMap) {
            if (key == userID) {
                val account: Account? = value
                userStore.token = account?.token
                userStore.userId = account?.userID
                break
            }
        }
    }

    private fun updateUserLocation(location: String) {
        val accountMap = userStore.getAccountsMapFromPreference()
        if (accountMap.containsKey(userStore.userId)) {
            val account: Account? = accountMap[userStore.userId]
            account?.location = location
            account?.let { it1 -> userStore.userId?.let { accountMap.put(it, it1) } }
            userStore.storeAccountsMapToPreference(accountMap)
        }
    }

}
