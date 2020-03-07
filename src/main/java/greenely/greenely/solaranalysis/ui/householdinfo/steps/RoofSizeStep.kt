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
import greenely.greenely.databinding.SolarAnalysisRoofSizeStepBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.tracking.Tracker
import javax.inject.Inject


class RoofSizeStep : androidx.fragment.app.Fragment(), Step {

    @VisibleForTesting
    lateinit var binding: SolarAnalysisRoofSizeStepBinding

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisRoofSizeStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel
    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Roof Size")

        when {
            binding.big.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_size_big)

            binding.medium.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_size_medium)

            binding.small.isChecked -> binding.image.setBackgroundResource(R.drawable.roof_size_small)
        }

        binding.big.setOnClickListener({
            if (binding.big.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_size_big)
        })
        binding.medium.setOnClickListener({
            if (binding.medium.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_size_medium)
        })
        binding.small.setOnClickListener({
            if (binding.small.isChecked)
                binding.image.setBackgroundResource(R.drawable.roof_size_small)
        })
    }

    override fun verifyStep(): VerificationError? {
        return if (viewModel.householdInfo.roofSizeId.get() == -1) {
            VerificationError(getString(R.string.roof_size_missing_error))
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
