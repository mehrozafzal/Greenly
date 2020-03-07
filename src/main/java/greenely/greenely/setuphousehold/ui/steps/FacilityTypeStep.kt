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
import greenely.greenely.databinding.SetupHouseholdFacilityTypeBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity
import greenely.greenely.setuphousehold.ui.SetupHouseholdErrorHandler
import greenely.greenely.setuphousehold.ui.SetupHouseholdViewModel
import greenely.greenely.setuphousehold.ui.model.HouseholdStep
import greenely.greenely.setuphousehold.ui.setInputOptions
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class FacilityTypeStep : androidx.fragment.app.Fragment(), BlockingStep {

    private lateinit var binding: SetupHouseholdFacilityTypeBinding
    private lateinit var viewModel: SetupHouseholdViewModel

    @Inject
    internal lateinit var errorHandler: SetupHouseholdErrorHandler

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var checkedOption: Int = -1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = SetupHouseholdFacilityTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[SetupHouseholdViewModel::class.java]
        binding.viewModel = viewModel

        setUpOptions()
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
        return if (viewModel.householdRequest.facilityTypeId.get() == null) {
            VerificationError(getString(greenely.greenely.R.string.facility_type_error))
        } else null
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        if (viewModel.householdRequest.facilityTypeId.get()
                == SetupHouseholdActivity.FACILITY_TYPE_APARTMENT) {
            callback.stepperLayout.setAdapter(
                    callback.stepperLayout.adapter,
                    HouseholdStep.ELECTRIC_CAR_COUNT.ordinal
            )
            callback.goToNextStep()
        } else callback.goToNextStep()
    }

    private fun setUpOptions() {
        viewModel.householdConfigurationOptions.observe(this, Observer {
            it?.let {

                if (viewModel.householdRequest.facilityTypeId.get()
                        != null && viewModel.householdRequest.facilityTypeId.get()!! > -1) {
                    checkedOption = viewModel.householdRequest.facilityTypeId.get()!!
                }

                binding.radioGroup.setInputOptions(it.facilityTypes, checkedOption)
            }
        })

        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            if (i > -1) viewModel.householdRequest.facilityTypeId.set(i)
        }


    }

    private fun trackScreen() {
        tracker.trackScreen("Onboarding Facility")
    }

}
