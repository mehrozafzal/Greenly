package greenely.greenely.solaranalysis.ui.householdinfo.steps

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.SolarAnalysisCompletedActivityBinding
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisViewModel
import greenely.greenely.solaranalysis.ui.householdinfo.charting.ChartManager
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class ResultStep : androidx.fragment.app.Fragment(), BlockingStep {
    @VisibleForTesting
    lateinit var binding: SolarAnalysisCompletedActivityBinding

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewModel: SolarAnalysisViewModel

    @Inject
    internal lateinit var tracker: Tracker

    @Inject
    internal lateinit var chartManager: ChartManager

    @Inject
    internal lateinit var errorHandler: SolarAnalysisErrorHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SolarAnalysisCompletedActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[SolarAnalysisViewModel::class.java]
        binding.viewModel = viewModel
    }

    override fun onSelected() {
        tracker.trackScreen("Solar Analysis Result")
        binding.yearlySavingHeading.text = getFormattedHeading()
        setUpChart()
    }

    override fun verifyStep(): VerificationError? = null

    override fun onError(error: VerificationError) {
        errorHandler.handleError(VerificationException(error))
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun setUpChart() {

        val analysis = viewModel.analysis.get()
                ?: run {
                    Log.e(javaClass.simpleName, "Analysis is null!")
                    return
                }

        chartManager.setUpChart(binding.chart, analysis.monthData)
    }

    private fun getFormattedHeading(): String {

        val analysis = viewModel.analysis.get()
                ?: run {
                    Log.e(javaClass.simpleName, "Analysis is null!")
                    return ""
                }

        return analysis.yearlySaving + getString(R.string.x_currency_per_year)
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback) {
        callback.goToPrevStep()
    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback) {
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback) {
        tracker.track(TrackerFactory().trackingEvent("sa_contact_form_seen", getString(R.string.solar_analysis_tracking_category)))
        callback.goToNextStep()
    }

}

