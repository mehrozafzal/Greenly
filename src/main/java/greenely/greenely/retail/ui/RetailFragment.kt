package greenely.greenely.retail.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailFragmentBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.retail.data.CustomerState
import greenely.greenely.retail.errors.RetailErrorHandler
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retail.models.RetailOverview
import greenely.greenely.retail.ui.charts.consumptiondatachart.ConsumptionChartSetupFactory
import greenely.greenely.retail.ui.charts.current_day_pricing_chart.CurrentDayPriceChartSetupFactory
import greenely.greenely.retail.ui.charts.next_day_pricing_chart.NextDayPriceChartSetupFactory
import greenely.greenely.retail.ui.charts.pricedatachart.PriceChartSetupFactory
import greenely.greenely.retail.ui.events.EventHandler
import greenely.greenely.retail.ui.events.EventHandlerFactory
import greenely.greenely.retailinvite.ui.RetailInviteViewModel
import greenely.greenely.retailinvite.ui.RetaiInviteActivity
import greenely.greenely.retailonboarding.ui.PriceSummaryViewModel
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.CommonUtils
import io.intercom.android.sdk.Intercom
import kotlinx.android.synthetic.main.retail_fragment.view.*
import kotlinx.android.synthetic.main.retail_overview_next_day_pricing.view.*
import javax.inject.Inject
import kotlin.math.abs

class RetailFragment : androidx.fragment.app.Fragment(), HasSnackbarView {
    override val snackbarView: View
        get() = binding.snackbarview

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var priceChartSetupFactory: PriceChartSetupFactory

    @Inject
    lateinit var currentDayPriceChartSetupFactory: CurrentDayPriceChartSetupFactory

    @Inject
    lateinit var nextDayPriceChartSetupFactory: NextDayPriceChartSetupFactory

    @Inject
    lateinit var errorHandler: RetailErrorHandler

    @Inject
    lateinit var consumptionChartSetupFactory: ConsumptionChartSetupFactory

    @Inject
    internal lateinit var eventHandlerFactory: EventHandlerFactory

    private lateinit var eventHandler: EventHandler
    private lateinit var viewModel: RetailViewModel
    private lateinit var binding: RetailFragmentBinding


    private val priceSummaryViewModel: PriceSummaryViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[PriceSummaryViewModel::class.java]
    }

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[RetailViewModel::class.java]

        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })
        viewModel.errors.observe(this, Observer {
            it?.let {
                errorHandler.handleError(it)
                binding.view.visibility = View.INVISIBLE
            }
        })

        bottomNavigationView = activity!!.findViewById(R.id.bottomNavigation)
        bottomNavigationView?.visibility = View.VISIBLE


        viewModel.retailOverview.observe(this, Observer {
            it?.let {
                when (it.state) {
                    CustomerState.EMPTY -> {
                        hideRetailOverviewView()
                        showRetailOnboardingStartScreen(RetailOnboardConfig(it.monthlyFee, it.discount, it.onBoardingAllowed, null))
                    }
                    CustomerState.TERMINATED -> {
                        hideRetailOverviewView()
                        showRetailOnboardingStartScreen(RetailOnboardConfig(it.monthlyFee, it.discount, it.onBoardingAllowed, null))
                    }
                    CustomerState.WAITING -> {
                        viewModel.fetchRetailState()
                        showRetailOnboardingDoneScreen()
                    }
                    CustomerState.FAILED -> {
                        hideRetailOverviewView()
                        showFailedScreen()
                        tracker.trackScreen("Retail Registration Failed")
                    }
                    CustomerState.COMPLETED -> {
                        viewModel.fetchRetailState()
                        setReferralBanner(it)
                        consumptionChartSetupFactory.createChartSetupWithData(it.currentMonth!!.points).applyToChart(binding.currentMonthChart.chart)
                        binding.currentMonthChart.cost.text = it.currentMonth.value
                        binding.currentMonthChart.detail.text = htmlText(it.currentMonth.subTitle)

                        currentDayPriceChartSetupFactory.createChartSetupWithData(it.currentDay!!.points).applyToChart(binding.currentDayChart.chart)

                        nextDayPriceChartSetupFactory.createChartSetupWithData(it.nextDay!!.points).applyToChart(binding.nextDayChart.chart)

                        binding.currentMonthChart.currency.visibility = View.GONE

                        tracker.trackScreen("Retail Overview")
                    }
                    CustomerState.OPERATIONAL -> {
                        setReferralBanner(it)
                        consumptionChartSetupFactory.createChartSetupWithData(it.currentMonth!!.points).applyToChart(binding.currentMonthChart.chart)
                        binding.currentMonthChart.cost.text = it.currentMonth.value
                        binding.currentMonthChart.detail.text = htmlText(it.currentMonth.subTitle)

                        currentDayPriceChartSetupFactory.createChartSetupWithData(it.currentDay!!.points).applyToChart(binding.currentDayChart.chart)

                        nextDayPriceChartSetupFactory.createChartSetupWithData(it.nextDay!!.points).applyToChart(binding.nextDayChart.chart)

                        tracker.trackScreen("Retail Overview")
                    }
                    else -> {
                        tracker.trackScreen("Retail Registration Failed")
                    }
                }

                if (it.hasUnpaidInvoices) {
                    binding.invoicesBadgeLayout.badge.setText("!")
                }

                if (binding.chartsLinearLayout.next_day_chart.chart.visibility == View.GONE) {
                    binding.chartsLinearLayout.next_day_chart.textView29.visibility = View.GONE
                    binding.chartsLinearLayout.next_day_chart.dataUnavailable.visibility = View.VISIBLE
                }
            }
        })

        binding.intercomBadgeLayout.layout.setOnClickListener {
            Intercom.client().displayMessenger()
        }

        binding.invoicesBadgeLayout.badge.setOnClickListener {
            hideRetailOverviewView()
            showInvoicesScreen()
        }

        binding.invoicesBadgeLayout.icon.setOnClickListener {
            hideRetailOverviewView()
            showInvoicesScreen()
        }

        setupCollapsingToolbar()

//        binding.intercomBadgeLayout.badge.setNumber(Intercom.client().unreadConversationCount, true)
//        Intercom.client().addUnreadConversationCountListener {
//            binding.intercomBadgeLayout.badge.setNumber(Intercom.client().unreadConversationCount, true)
//        }

        binding.intercomBadgeLayout.badge.setNumber(10)

    }


    private fun setReferralBanner(retailOverview: RetailOverview) {
        binding.inviteFriendBanner.txtInviteFriendBannerSubTitle.setText(String.format(getString(R.string.invite_friends_banner_sub_title), CommonUtils.getCurrencyFormat(retailOverview.discount)))
        binding.inviteFriendBanner.referralBanner.setOnClickListener {
            trackRetailEvent("Retail Overview", "clicked_referral_invite")
            navigateToRetailInviteScreen()
        }
    }

    private fun navigateToRetailInviteScreen() {
        startActivity(Intent(context, RetaiInviteActivity::class.java))
    }

    private fun setupCollapsingToolbar() {
        binding.appbar.setExpanded(true)

        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                // Collapsed
                binding.toolbarTitle.visibility = View.VISIBLE
            } else {
                // Expanded
                binding.toolbarTitle.visibility = View.GONE
            }
        })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun showFailedScreen() {
        childFragmentManager
                .beginTransaction()
                .replace(R.id.view, FailedStateFragment())
                .commitNow()
    }

    private fun showInvoicesScreen() {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.add(R.id.content, RetailInvoicesFragment())
                ?.setCustomAnimations(R.anim.slide_up_animation, R.anim.slide_down_animation)
                ?.commitNow()
    }

    private fun showRetailOnboardingDoneScreen() {
        activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content, RetailOnboardingDoneFragment())
                ?.commitNow()
    }

    private fun showRetailOnboardingStartScreen(retailOboardConfig: RetailOnboardConfig) {
        val bundle = Bundle()
        val fragment = BecomeCustomerFragment()
        priceSummaryViewModel.fetchPriceSummary()
        bundle.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOboardConfig)
        fragment.arguments = bundle
        childFragmentManager
                .beginTransaction()
                .replace(R.id.snackbarview, fragment)
                .commitNow()
    }

    private fun getActionBarSize(): Int? {
        val tv = TypedValue()
        activity?.let {
            if (it.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            }
        }
        return null
    }

    private fun hideRetailOverviewView() {
        binding.appbar.visibility = View.GONE
        binding.nestedChartScrollView.visibility = View.GONE
    }

    private fun htmlText(text: String): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            return Html.fromHtml(text)
        }
    }

    private fun trackRetailEvent(label: String, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Retail",
                label
        ))
    }

}
