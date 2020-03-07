package greenely.greenely.retailonboarding.ui

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.R
import greenely.greenely.databinding.RetailOnboardingStepsActivityBinding
import greenely.greenely.retailonboarding.ui.util.PriceSummaryFlow
import greenely.greenely.retailonboarding.ui.util.RedeemCodeFlow
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.steps.AddressStepFragment
import greenely.greenely.retailonboarding.steps.PriceSummaryFragment
import greenely.greenely.retailonboarding.steps.RetailRedeemCodeFragment
import greenely.greenely.retailonboarding.ui.events.EventHandler
import greenely.greenely.retailonboarding.ui.events.EventHandlerFactory
import greenely.greenely.retailonboarding.ui.util.CombinedPOAFlow
import greenely.greenely.signature.OnboardCompleteListener
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.isApiProduction
import javax.inject.Inject

class RetailOnboardingActivity @Inject constructor() : AppCompatActivity(), HasSupportFragmentInjector,OnboardCompleteListener {


    @Inject
    internal lateinit var tracker: Tracker

    override fun onboardCompleted() {
        tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_done),getString(R.string.poa_category),"Combined Process"))
    }

    companion object {
        val CLOSE_ONBOARDING_ACTIVITY_REQUEST_CODE = 2

        fun initiateCombinedPOAFlow( context: Context,personalNumber:String) : Intent{
            return Intent(context,RetailOnboardingActivity::class.java).apply {
                putExtra(RetailOnboardConfig.KEY_CONFIG,RetailOnboardConfig(null,0f,true,CombinedPOAFlow(null,personalNumber )))
            }
        }
    }

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @Inject
    lateinit var eventHandlerFactory: EventHandlerFactory

    internal lateinit var eventHandler: EventHandler

    internal lateinit var binding: RetailOnboardingStepsActivityBinding

    private lateinit var viewModel: RetailOnboardingViewModel

    private val config by lazy {
        intent?.getSerializableExtra(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.retail_onboarding_steps_activity)

        if (savedInstanceState == null) {
            var retailOnboardConfig=intent.extras.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
            var flow = retailOnboardConfig.flow

            when (flow) {
                is PriceSummaryFlow -> {
                    var fragment = PriceSummaryFragment()
                    fragment.arguments = Bundle()
                    fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content, fragment)
                            .commit()
                }

                is RedeemCodeFlow -> {
                    var fragment = RetailRedeemCodeFragment()
                    fragment.arguments = Bundle()
                    fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content, fragment)
                            .commit()

                }
                is CombinedPOAFlow -> {
                    var fragment = CombinedPOAAdressStepFragment()
                    fragment.arguments = Bundle()
                    fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.content, fragment)
                            .commit()

                }

            }


        }

        eventHandler = eventHandlerFactory.createEventHandler(this)

        viewModel = ViewModelProviders.of(this, factory)[RetailOnboardingViewModel::class.java]

        binding.viewModel = viewModel

        viewModel.events.observe(this, Observer {
            it?.let { eventHandler.handleEvent(it) }
        })

        binding.toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        if (!isApiProduction()) {
            viewModel.customerInfoInput.personalNumber.set("19521109-329")
            //viewModel.customerInfoInput.address.set("Random street 41")
            //viewModel.customerInfoInput.postalCode.set("12345")
            //viewModel.customerInfoInput.postalRegion.set("Stockholm")
            viewModel.customerInfoInput.email.set("eiman@greenely.se")
            viewModel.customerInfoInput.phoneNumber.set("0712345678")
            viewModel.customerInfoInput.powerOfAttorneyTermsAccepted.set(true)
            viewModel.customerInfoInput.userTermsAccepted.set(true)
        }

    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = dispatchingFragmentInjector

    override fun onDestroy() {
        super.onDestroy()
        //Let MainActivity know that RetailOnboardingActivity was closed with a "back" button, not with a "continue" button
        if (!this.isFinishing && !this.isDestroyed) {
            this.setResult(Activity.RESULT_CANCELED, Intent(this, RetailOnboardingActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        supportFragmentManager.popBackStack()

        config.flow?.let {
            when (it) {
                is CombinedPOAFlow -> {
                    overridePendingTransition(R.anim.enter_from_left,R.anim.exit_to_right)

                }

            }
        }
    }

    override fun onPause() {
        super.onPause()

    }
}

