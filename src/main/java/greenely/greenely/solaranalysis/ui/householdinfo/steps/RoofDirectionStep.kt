package greenely.greenely.solaranalysis.ui.householdinfo.steps

import androidx.lifecycle.Observer
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
import greenely.greenely.R
import greenely.greenely.databinding.SolarAnalysisRoofDirectionStepBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject


class RoofDirectionStep : androidx.fragment.app.Fragment(), BlockingStep {

    @VisibleForTesting
    lateinit var binding: SolarAnalysisRoofDirectionStepBinding

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisRoofDirectionStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel
    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Roof Orientation")

        when {
            binding.west.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_direction_west)

            binding.east.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_direction_east)

            binding.south.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_direction_south)

            binding.southWest.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_direction_southwest)

            binding.southEast.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_direction_southeast)

        }

        binding.west.setOnClickListener({
            if (binding.west.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_direction_west)
        })
        binding.east.setOnClickListener({
            if (binding.east.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_direction_east)
        })
        binding.south.setOnClickListener({
            if (binding.south.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_direction_south)
        })
        binding.southEast.setOnClickListener({
            if (binding.southEast.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_direction_southeast)
        })
        binding.southWest.setOnClickListener({
            if (binding.southWest.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_direction_southwest)
        })
    }

    override fun verifyStep(): VerificationError? {
        return if (viewModel.householdInfo.roofDirectionId.get() == -1) {
            VerificationError(getString(R.string.roof_direction_missing_error))
        } else {
            null
        }
    }

    override fun onError(error: VerificationError) {
        errorHandler.handleError(VerificationException(error))
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        tracker.track(TrackerFactory().trackingEvent("sa_result_seen",getString(R.string.solar_analysis_tracking_category)))
        viewModel.getAnalysisDetails().observe(this, Observer {
            it?.let {
                callback.goToNextStep()
            }
        })
    }

}

