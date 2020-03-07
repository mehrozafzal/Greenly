package greenely.greenely.retail.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailBecomeCustomerBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retail.ui.events.ClickEvents
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.retailonboarding.ui.util.PriceSummaryFlow
import greenely.greenely.retailonboarding.ui.util.RedeemCodeFlow
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.onPageSelected
import kotlinx.android.synthetic.main.retail_become_customer.*
import javax.inject.Inject

class BecomeCustomerFragment :Fragment(), HasSnackbarView {
    override val snackbarView: View
        get() = binding.pager

    private lateinit var binding: RetailBecomeCustomerBinding

    @Inject
    lateinit var tracker: Tracker

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private var monthlyFee: String? = null
    private var onboardingAllowed: Boolean = false

    private lateinit var viewModel: RetailViewModel

    private lateinit var retailOnBoardConfig: RetailOnboardConfig

    private val adapterRetailStart: RetailStartViewPagerAdapter by lazy {
        RetailStartViewPagerAdapter(childFragmentManager,this)
    }

    private val adapterRetailComingSoon: RetailComingSoonViewPagerAdapter by lazy {
        RetailComingSoonViewPagerAdapter(childFragmentManager,this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_become_customer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailViewModel::class.java]


        viewModel.clickEvents.observe(this, Observer {
            it?.let {
                handleClickEvent(it)

            }
        })


        arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG)?.let {
            when (it) {
                is RetailOnboardConfig -> {
                    retailOnBoardConfig = it
                    onboardingAllowed = it.onBoardingAllowed
                    monthlyFee = it.monthlyFee
                }

            }

        }

        pager.orientation=ViewPager2.ORIENTATION_VERTICAL
        if (onboardingAllowed) {
            prepareOnboardingScreen()
        } else {
            prepareCommingSoonScreen()
        }

        dots.attachViewPager(pager)
        dots.setDotTintRes(R.color.green_1)

        pager.onPageSelected {
            val colorRes = when (it) {
                0 -> R.color.green_1
                1 -> R.color.white
                2 -> R.color.white
                else -> R.color.green_1
            }
            context?.let {
                dots.setDotTintRes(colorRes)
            }

        }

    }

    private fun prepareOnboardingScreen() {
        tracker.trackScreen("Retail Start")

        pager.adapter = adapterRetailStart
        pager.offscreenPageLimit = 4


    }


    private fun trackEvent(label: String, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Retail",
                label

        ))

    }

    private fun handleClickEvent(clickEvent: ClickEvents) {
        when (clickEvent) {

            ClickEvents.RETAIL_START_PROMOCODE_TOP_BUTTON_CLICK -> {

                trackEvent("Retail Start Top button", "clicked_have_referral_code")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = RedeemCodeFlow(null) })

            }
            ClickEvents.RETAIL_START_TOP_CONTINUE_CLICK -> {
                trackEvent("Top button")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = PriceSummaryFlow(null) })
            }

            ClickEvents.RETAIL_START_PROMOCODE_BOTTOM_BUTTON_CLICK -> {
                trackEvent("Retail Start Bottom button", "clicked_have_referral_code")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = RedeemCodeFlow(null) })
            }

            ClickEvents.RETAIL_START_BOTTOM_CONTINUE_CLICK -> {
                trackEvent("Bottom button")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = PriceSummaryFlow(null) })
            }

            ClickEvents.COMING_SOON_PROMOCODE_TOP_BUTTON_CLICK -> {

                trackEvent("Coming Soon Screen Top button", "clicked_have_referral_code")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = RedeemCodeFlow(null) })

            }

            ClickEvents.COMING_SOON_PROMOCODE_BOTTOM_BUTTON_CLICK -> {
                trackEvent("Coming Soon Screen Bottom button", "clicked_have_referral_code")
                startOnboardingAndWaitForResult(retailOnBoardConfig.apply { flow = RedeemCodeFlow(null) })
            }
            ClickEvents.COMING_SOON_TOP_INTERESTED_CLICK -> {

                trackRetailInterestSent("Top button")
                showSnackBar(R.string.retail_interest_report_sent)

            }
            ClickEvents.COMING_SOON_BOTTOM_INTERESTED_CLICK -> {

                trackRetailInterestSent("Bottom button")
                showSnackBar(R.string.retail_interest_report_sent)

            }


        }
    }


    private fun prepareCommingSoonScreen() {

        viewModel.initOnboardingInterestVisibility()
        viewModel.discount.value=retailOnBoardConfig.discount

        tracker.trackScreen("Retail Coming Soon")

        pager.adapter = adapterRetailComingSoon
        pager.offscreenPageLimit = 4


    }


    private fun showSnackBar(message: Int) {
        Snackbar.make(
                snackbarView,
                message,
                Snackbar.LENGTH_LONG)
                .show()
    }

    private fun trackRetailInterestSent(label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                "retail_interest_sent",
                "Retail",
                label
        ))
    }

    private fun trackEvent(label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                "retail_started",
                "Retail",
                label
        ))
    }

    //
    private fun startOnboardingAndWaitForResult(retailOnboardConfig: RetailOnboardConfig) {

        var intent = Intent(context, RetailOnboardingActivity::class.java).apply {
            putExtra(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
        }
        activity?.startActivityForResult(intent, RetailOnboardingActivity.CLOSE_ONBOARDING_ACTIVITY_REQUEST_CODE)
        activity?.overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change)
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)

    }


    class RetailStartViewPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager,fragment:Fragment) : FragmentStateAdapter(fragmentManager,fragment.lifecycle) {

        override fun createFragment(position: Int): Fragment=
                when (position) {
            0 -> RetailStartFragment()
            1 -> RetailSecondFragment()
            2 -> RetailThirdFragment()
            3 -> RetailStartFourthFragment()
            else -> throw IllegalArgumentException("Illegal arguement for RetailStartViewPagerAdapter")
        }


        override fun getItemCount(): Int =4


    }


    class RetailComingSoonViewPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager,fragment:Fragment) : FragmentStateAdapter(fragmentManager,fragment.lifecycle) {


        override fun createFragment(position: Int): Fragment=
                when (position) {
                    0 -> RetailComingSoonFragment()
                    1 -> RetailSecondFragment()
                    2 -> RetailThirdFragment()
                    3 -> RetailComingSoonFourthFragment()
                    else -> throw IllegalArgumentException("Illegal arguement for RetailStartViewPagerAdapter")
                }


        override fun getItemCount(): Int =4





    }



}