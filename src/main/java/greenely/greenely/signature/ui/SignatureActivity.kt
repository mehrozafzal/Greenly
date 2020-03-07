package greenely.greenely.signature.ui

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.databinding.SignatureActivityBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.steps.PriceSummaryFragment
import greenely.greenely.signature.OnboardCompleteListener
import greenely.greenely.signature.ui.events.SignatureEventHandler
import greenely.greenely.signature.ui.steps.AddressStep
import greenely.greenely.signature.ui.steps.PersonalNumberStep
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.isApiProduction
import greenely.greenely.utils.replaceCurrentFragment
import javax.inject.Inject

@OpenClassOnDebug
class SignatureActivity : AppCompatActivity(), HasSupportFragmentInjector, HasSnackbarView, OnboardCompleteListener,
        SignatureListener {
    override fun onboardCompleted() {
        tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_done),getString(R.string.poa_category),"signature successful"))
    }

    override val snackbarView: View
        get() = binding.coordinatorLayout

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var eventHandler: SignatureEventHandler

    @Inject
    lateinit var errorHandler: SignatureErrorHandler

    private lateinit var viewModel: SignatureViewModel
    private lateinit var binding: SignatureActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.signature_activity)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SignatureViewModel::class.java]
        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })
        viewModel.errors.observe(this, Observer { it?.let { errorHandler.handleError(it) } })

        supportFragmentManager.replaceCurrentFragment(PersonalNumberStep())
        binding.viewModel = viewModel

        if (!isApiProduction()) {
            viewModel.signatureInput.personalNumber.set("19521109-329")
        }
    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = dispatchingFragmentInjector

    fun trackPoaSigned() {
//        if (viewModel.signatureInput.wantsManuallFacilityId.get())
//            tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_signed), getString(R.string.poa_category), getString(R.string.poa_with_meter)))
//        else
//            tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_signed), getString(R.string.poa_category), getString(R.string.poa_without_meter)))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
             RETAIL_ONBOARD_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }


    }

    override fun proceedToNextStep() {
//        binding.stepper.proceed()
    }

    companion object {
        const val RETAIL_ONBOARD_REQUEST_CODE=12
    }
}

