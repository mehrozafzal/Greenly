package greenely.greenely.solaranalysis.ui.householdinfo.steps

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.SolarAnalysisAddressStepBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.tracking.Tracker
import org.grunkspin.swedishformats.android.formatAsPostalCode
import javax.inject.Inject

class AddressStep : androidx.fragment.app.Fragment(), BlockingStep {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    lateinit var tracker: Tracker

    @VisibleForTesting
    lateinit var binding: SolarAnalysisAddressStepBinding

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisAddressStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel

        binding.postalCode.formatAsPostalCode()
    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Address")
    }

    override fun verifyStep(): VerificationError? {
        activity!!.hideKeyboard()
        val validationErrors = viewModel.validateAddressInput()
        return if (validationErrors.hasErrors()) {
            VerificationError("") // We don't care about the message
        } else null
    }

    override fun onError(error: VerificationError) {
        // Nothing to do.
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
        callback.complete()
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        activity!!.hideKeyboard()
        viewModel.validateAddress {
            if (it == null) {
                callback.goToNextStep()
            } else {
                errorHandler.handleError(it)
            }
        }
    }
}

