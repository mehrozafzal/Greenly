package greenely.greenely.main.ui

import androidx.lifecycle.*
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import coil.api.load
import coil.transform.CircleCropTransformation
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.databinding.MainActivityBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.home.ui.HomeViewModel
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.events.EventHandler
import greenely.greenely.main.events.EventHandlerFactory
import greenely.greenely.main.router.Route
import greenely.greenely.main.router.Router
import greenely.greenely.main.router.RouterFactory
import greenely.greenely.models.Resource
import greenely.greenely.profile.model.Account
import greenely.greenely.profile.ui.UpdateProfileViewModel
import greenely.greenely.retail.ui.RetailInvoicesBackPressedListener
import greenely.greenely.retail.ui.RetailOnboardingDoneFragment
import greenely.greenely.retail.ui.RetailViewModel
import greenely.greenely.retailonboarding.models.RetailOnboardingState
import greenely.greenely.settings.ui.SettingsBackPressedListener
import greenely.greenely.settings.ui.SettingsViewModel
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity.Companion.REONBOARDING_COMPLETED
import greenely.greenely.store.UserStore
import org.grunkspin.notificationdotteddrawable.NotificationDottedDrawable
import javax.inject.Inject

@OpenClassOnDebug
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, HasSnackbarView, IMainAcitvityListener {


    override val snackbarView: View
        get() = binding.content

    companion object {
        val TARGET_KEY = "target"
        val SIGN_POA_REQUEST = 1

        fun getStartIntent(context: Context, target: Route = Route.HOME): Intent =
                Intent(context, MainActivity::class.java).apply {
                    putExtra(TARGET_KEY, target.name)
                }
    }

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var userStore: UserStore

    @Inject
    internal lateinit var routerFactory: RouterFactory
    private lateinit var router: Router

    @Inject
    internal lateinit var eventHandlerFactory: EventHandlerFactory
    private lateinit var eventHandler: EventHandler

    private lateinit var binding: MainActivityBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, factory)[MainViewModel::class.java]
    }

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var retailViewModel: RetailViewModel

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    private var reonboardingCompleted: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        createViewModels()
        setUpEventHandler()
        setUpRouter()
        setUpFeedNotificationDot()
        observeProfileResponse()

        reonboardingCompleted = intent.getBooleanExtra(REONBOARDING_COMPLETED, false)

        if (reonboardingCompleted) {
            Snackbar.make(snackbarView, getString(R.string.reonboarding_your_househoald_parameters_have_been_updated), Snackbar.LENGTH_LONG).show()
        }

        retailViewModel.fetchRetailOnboardingState().observe(this, Observer { handleRetailOnboardingState(it) })
        viewModel.fetchDeepLinkScreenState().observe(this, Observer { handleDeepLinkScreenRoute(it) })
    }

    override fun supportFragmentInjector() = fragmentInjector

    private fun createViewModels() {
        historyViewModel = ViewModelProviders.of(this, factory)[HistoryViewModel::class.java]
        homeViewModel = ViewModelProviders.of(this, factory)[HomeViewModel::class.java]
        settingsViewModel = ViewModelProviders.of(this, factory)[SettingsViewModel::class.java]
        retailViewModel = ViewModelProviders.of(this, factory)[RetailViewModel::class.java]
        retailViewModel.fetchRetailState()
    }

    private fun setUpRouter() {
        router = routerFactory.create(binding.bottomNavigation)
        router.onRouteSelectedListener = viewModel::onRouteSelected
    }

    private fun setUpEventHandler() {
        eventHandler = eventHandlerFactory.create()
        viewModel.events.observe(this, Observer {
            it?.let {
                eventHandler.handleEvent(it)
            }
        })
    }

    private fun setUpFeedNotificationDot() {
        val menuItem = binding.bottomNavigation.menu.getItem(3)
        menuItem.icon = NotificationDottedDrawable(this, R.drawable.feed_icon).apply {
            hasDot = false
        }

        viewModel.hasUnreadFeedItems.observe(this, Observer {
            it?.let {
                menuItem.icon = NotificationDottedDrawable(this, R.drawable.feed_icon).apply {
                    hasDot = it
                }
            }
        })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.getParcelable<Router.RouterState>(Router.STATE_KEY)?.let {
            router.state = it
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Router.STATE_KEY, router.state)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        historyViewModel.onActivityResult(requestCode, resultCode, data)
        homeViewModel.onActivityResult(requestCode, resultCode, data)
        retailViewModel.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleDeepLinkScreenRoute(deepLinkScreenRoute: Route?) {
        if (deepLinkScreenRoute == null)
            return
        if (userStore.isInvitedUser!!)
            router.routeTo(Route.COMPETE_FRIEND)
        else
            router.routeTo(deepLinkScreenRoute)
    }

    private fun handleRetailOnboardingState(retailOnboardingState: RetailOnboardingState?) {
        when (retailOnboardingState) {
            RetailOnboardingState.RETAIL_ONBOARDING_COMPLETED -> openRetailOnboardingDoneFragment()
        }
    }

    private fun openRetailOnboardingDoneFragment() {
        routeTo(Route.RETAIL)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, RetailOnboardingDoneFragment())
                .commit()
    }

    override fun onBackPressed() {
        when (val currentFragment = this.supportFragmentManager.findFragmentById(R.id.content)) {
            is SettingsBackPressedListener -> currentFragment.handleBackPressed()
            is RetailInvoicesBackPressedListener -> currentFragment.handleBackPressed()

            else -> super.onBackPressed()
        }
    }

    private fun observeProfileResponse() {
        viewModel.getProfileResponse()
        viewModel.profileGetResponseLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    userStore.firstName = it.value.firstName
                    userStore.lastName = it.value.lastName
                    userStore.avatar = it.value.avatarUrl
               /*     val accountMap = userStore.getAccountsMapFromPreference()
                    if (it.value.firstName != null && it.value.lastName != null) {
                        if (accountMap.containsKey(userStore.userId)) {
                            val account: Account? = accountMap[userStore.userId]
                            account?.name = it.value.firstName + " " + it.value.lastName
                            userStore.userId?.let { it1 -> account?.let { it2 -> accountMap.put(it1, it2) } }
                            userStore.storeAccountsMapToPreference(accountMap)
                        }
                    }*/
                }
                is Resource.Error -> {
                    //Log.d("ERROR", it.error.message)
                }
            }
        })
    }

    override fun routeTo(route: Route) {
        router.routeTo(route)
    }

}
