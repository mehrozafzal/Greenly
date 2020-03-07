package greenely.greenely.setuphousehold.ui.steps

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.databinding.SetupHouseholdIntroBinding
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class IntroStep : androidx.fragment.app.Fragment(), BlockingStep {

    private lateinit var binding: SetupHouseholdIntroBinding
    private lateinit var viewModel: SetupHouseholdViewModel

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SetupHouseholdIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[SetupHouseholdViewModel::class.java]
        binding.viewModel = viewModel

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onSelected() {
        setUpTexts()
        trackScreen()
    }

    override fun onError(error: VerificationError) {
    }

    override fun verifyStep(): VerificationError? = null

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        viewModel.abortSetup()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        callback.goToNextStep()
    }

    private fun setUpTexts() {
        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {
                binding.introText.text = it.introText
                binding.introTitle.text = it.introTitle
            }
        })
    }

    private fun trackScreen() {
        tracker.trackScreen("Onboarding Intro")
    }

}
