package greenely.greenely.signature.ui.steps

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.SignatureSigningStepBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.signature.ui.SignatureViewModel
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class SigningStep : androidx.fragment.app.Fragment(), BlockingStep {

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SignatureViewModel

    private lateinit var binding: SignatureSigningStepBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SignatureSigningStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[SignatureViewModel::class.java]
        binding.viewModel = viewModel

        //TODO: After updating viewModel error state, UI error state should update as well
        binding.clearSignatureDrawingViewButton.setOnClickListener {
            binding.signatureDrawingView.reset()
            binding.signingInstructionsTextView.setTextColor(Color.parseColor("#89000000"))
        }
    }

    override fun onSelected() {
        tracker.trackScreen("PoA Signature")
        activity?.hideKeyboard()
    }

    override fun verifyStep(): VerificationError? {
        val validationErrors = viewModel.validateSigningStep()
        return if (false) {
            VerificationError("") // We don't care about the message
        } else {
            binding.signingInstructionsTextView.setTextColor(Color.parseColor("#89000000"))
            null
        }
    }

    override fun onError(error: VerificationError) {
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback?) {
        callback?.goToPrevStep()
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?) {
        callback?.goToNextStep()

    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        // Do nothing
    }
}

