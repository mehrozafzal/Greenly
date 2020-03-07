package greenely.greenely.history.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryChartHolderFragmentBinding
import greenely.greenely.databinding.HistoryConsumptionChartFragmentBinding
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.models.HeaderModel
import greenely.greenely.history.views.chart.PriceChartFragment
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import org.joda.time.DateTime
import javax.inject.Inject

class ChartHolderFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a [ConsumptionChartFragment] for a specified [date].
         */
        fun create(date: DateTime, headerModel: HeaderModel): ChartHolderFragment =
                ChartHolderFragment().apply {
                    this.date = date
                    this.headerModel = headerModel
                }
    }

    private var date: DateTime? = null

    private lateinit var headerModel: HeaderModel


    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HistoryViewModel

    private lateinit var binding: HistoryChartHolderFragmentBinding

    @Inject
    lateinit var tracker: Tracker


    /**
     * Create the view and the binding.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_chart_holder_fragment, container, false)

        return binding.root
    }


    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.viewModel = viewModel


        binding.chartSelector.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != viewModel.selectedChart.get()) {
                trackGraph(checkedId)
                date?.let {
                    viewModel.getDataForChart(checkedId, it)

                    var fragment = getFragmentForChart(checkedId, it)
                    childFragmentManager?.beginTransaction()
                            ?.replace(R.id.chartsContainer, fragment)
                            ?.commit()
                }
            }
        }

        setChartFragment(viewModel.selectedChart.get())
        binding.chartSelector.check(viewModel.selectedChart.get())
    }

    fun setChartFragment(id: Int) {
        date?.let {
            var fragment = getFragmentForChart(id, it)
            childFragmentManager?.beginTransaction()
                    ?.replace(R.id.chartsContainer, fragment)
                    ?.commit()
        }

    }


    fun getFragmentForChart(id: Int, date: DateTime) =
            when (id) {
                R.id.totalChart -> {
                    ConsumptionChartFragment.create(date, headerModel)
                }
                R.id.temperatureChart -> {
                    TemperatureChartFragment.create(date, headerModel)
                }
                else -> {
                    PriceChartFragment.create(date, headerModel)
                }
            }


    fun trackGraph(id: Int) {
        when (id) {
            R.id.totalChart -> {

                trackHistoryEvent(null, "clicked_total_graph")

            }
            R.id.temperatureChart -> {
                trackHistoryEvent(null, "clicked_temperature_graph")

            }
            else -> {
                trackHistoryEvent(null, "clicked_price_graph")

            }

        }
    }

    private fun trackHistoryEvent(label: String?, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "History",
                label

        ))
    }


}