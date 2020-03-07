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
import android.widget.ArrayAdapter
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.SetupHouseholdMunicipilityBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.setuphousehold.ui.SetupHouseholdErrorHandler
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class MunicipalityStep : androidx.fragment.app.Fragment(), BlockingStep {

    private lateinit var binding: SetupHouseholdMunicipilityBinding
    private lateinit var viewModel: SetupHouseholdViewModel

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var errorHandler: SetupHouseholdErrorHandler

    @Inject
    lateinit var tracker: Tracker

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SetupHouseholdMunicipilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[SetupHouseholdViewModel::class.java]
        binding.viewModel = viewModel

        setAdapter()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onSelected() {
        trackScreen()
    }

    override fun onError(error: VerificationError) {
        errorHandler.handleError(VerificationException(error))
    }

    override fun verifyStep(): VerificationError? {
        viewModel.setMunicipalityId()
        return if (viewModel.householdRequest.municipalityId.get() == null) {
            VerificationError(getString(R.string.please_check_municipality))
        } else null
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        activity?.finish()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        activity!!.hideKeyboard()
        callback.goToNextStep()
    }

    private fun setAdapter() {
        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {
                binding.municipality.setAdapter(
                        ArrayAdapter(
                                context,
                                android.R.layout.simple_dropdown_item_1line,
                                it.municipalities.map { it.name }
                        )
                )
                if (viewModel.shouldReOnboard) {
                    //prefill
                    viewModel.householdConfigurationOptions.value?.municipalities?.let {
                        val option = it.findLast { it.id == viewModel.householdRequest.municipalityId.get() }
                        if (option != null) {
                            viewModel.municipality.set(option.name)
                        }
                    }
                }
            }
        })
    }

    private fun trackScreen() {
        tracker.trackScreen("Onboarding Municipality")
    }

}
