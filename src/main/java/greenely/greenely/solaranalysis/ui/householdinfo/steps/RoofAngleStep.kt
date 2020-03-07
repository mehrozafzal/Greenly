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
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.SolarAnalysisRoofAngleStepBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class RoofAngleStep : androidx.fragment.app.Fragment(), Step {
    @VisibleForTesting
    lateinit var binding: SolarAnalysisRoofAngleStepBinding

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisRoofAngleStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel

    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Roof Angle")

        when {
            binding.tightAngle.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_angle_tight)

            binding.mediumAngle.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_angle_medium)

            binding.wideAngle.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_angle_wide)
        }

        binding.tightAngle.setOnClickListener({
            if (binding.tightAngle.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_angle_tight)
        })
        binding.mediumAngle.setOnClickListener({
            if (binding.mediumAngle.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_angle_medium)
        })
        binding.wideAngle.setOnClickListener({
            if (binding.wideAngle.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_angle_wide)
        })
    }

    override fun verifyStep(): VerificationError? {
        return if (viewModel.householdInfo.roofAngleId.get() == -1) {
            VerificationError(getString(R.string.roof_angle_missing_error))
        } else {
            null
        }
    }

    override fun onError(error: VerificationError) {
        errorHandler.handleError(VerificationException(error))
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

