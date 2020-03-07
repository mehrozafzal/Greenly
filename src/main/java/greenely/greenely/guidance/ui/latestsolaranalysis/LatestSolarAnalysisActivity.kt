package greenely.greenely.guidance.ui.latestsolaranalysis

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.LatestSolarAnalysisActivityBinding
import greenely.greenely.guidance.models.LatestSolarAnalysis
import greenely.greenely.guidance.ui.GuidanceViewModel
import greenely.greenely.guidance.ui.latestsolaranalysis.charting.ChartManager
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class LatestSolarAnalysisActivity : AppCompatActivity() {

    lateinit var binding: LatestSolarAnalysisActivityBinding
    private lateinit var viewModel: GuidanceViewModel

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var chartManager: ChartManager

    @Inject
    lateinit var tracker: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.latest_solar_analysis_activity)
        viewModel = ViewModelProviders.of(this, factory)[GuidanceViewModel::class.java]

        viewModel.guidanceContent.observe(this, Observer {
            it?.let {
                if (it.latestSolarAnalysis != null) {
                    setUpAnalysisData(it.latestSolarAnalysis)
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Solar Analysis Previous Result")
    }

    private fun setUpAnalysisData(data: LatestSolarAnalysis) {
        setUpChart(data)
        binding.latestSolarAnalysis = data
        binding.yearlySavingHeading.text = getFormattedHeading(data.yearlySaving)
    }

    private fun setUpChart(data: LatestSolarAnalysis) {
        chartManager.setUpChart(binding.chart, data.monthData)
    }

    private fun getFormattedHeading(value: String?): String? {
        return value + getString(R.string.x_currency_per_year)
    }

}