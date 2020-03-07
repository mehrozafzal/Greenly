package greenely.greenely.history.views

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryDistributionFragmentBinding
import greenely.greenely.history.DistributionDataPoint
import greenely.greenely.history.HistoryViewModel
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment for showing distribution
 */
class DistributionFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a distribution fragment for a specified [date].
         */
        fun create(date: DateTime): DistributionFragment =
                DistributionFragment().apply {
                    this.date = date
                }
    }

    private var date: DateTime? = null

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: HistoryDistributionFragmentBinding

    private lateinit var viewModel: HistoryViewModel

    /**
     * Create the binding and the view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_distribution_fragment, container, false)

        return binding.root
    }

    /**
     * Create and bind to the view model.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        viewModel.isDistributionLoading().observe(this, Observer { it?.let { setLoading(it) } })

        date?.let {
            viewModel.getDistribution(it).observe(this, Observer {
                it?.let { binding.chartData = ChartData(it[0], it[1], it[2]) }
            })
        }

        binding.distributionProgress.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    private var lastVisibility = View.GONE

                    /**
                     * Update the visibility of the content when the visibility of the
                     * progress updates.
                     */
                    override fun onGlobalLayout() {
                        if (binding.distributionProgress.visibility != lastVisibility) {
                            if (binding.distributionProgress.visibility == View.VISIBLE) {
                                binding.distributionContent.visibility = View.INVISIBLE
                            } else {
                                binding.distributionContent.visibility = View.VISIBLE
                            }
                            lastVisibility = binding.distributionProgress.visibility
                        }
                    }
                }
        )

        binding.viewModel = viewModel
    }

    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            clearCharts()
            binding.distributionProgress.show()
        } else {
            binding.distributionProgress.hide()
            if (binding.distributionProgress.visibility == View.GONE) {
                binding.distributionContent.visibility = View.VISIBLE
            }
        }
    }

    private fun clearCharts() {
        binding.distributionContent.visibility = View.INVISIBLE
        binding.chartData = ChartData(
                DistributionDataPoint("", "", 0),
                DistributionDataPoint("", "", 0),
                DistributionDataPoint("", "", 0)
        )
    }

    /**
     * Model class of the data for the charts.
     */
    data class ChartData(
            val night: DistributionDataPoint,
            val day: DistributionDataPoint,
            val evening: DistributionDataPoint
    )
}