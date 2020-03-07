package greenely.greenely.history.views

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryMinMaxFragmentBinding
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.MinMaxDataPoint
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Chart for displaying min and max values.
 */
class MinMaxFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a [MinMaxFragment] for a specified [date].
         */
        fun create(date: DateTime): MinMaxFragment = MinMaxFragment().apply {
            this.date = date
        }
    }

    private var date: DateTime? = null

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: HistoryMinMaxFragmentBinding

    private lateinit var viewModel: HistoryViewModel

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_min_max_fragment, container, false)

        return binding.root
    }


    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    /**
     * Create and bind to the view model
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.minMaxChart.styleChart()

        viewModel.isMinMaxLoading().observe(this, Observer { it?.let { setLoading(it) } })

        binding.minMaxProgress.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {

                    private var lastVisibility = View.GONE

                    /**
                     * Update the visibility of the content depending of the
                     * visibility of the loader.
                     */
                    override fun onGlobalLayout() {
                        if (lastVisibility != binding.minMaxProgress.visibility) {
                            if (binding.minMaxProgress.visibility == View.VISIBLE) {
                                binding.minMaxContent.visibility = View.INVISIBLE
                            } else {
                                binding.minMaxContent.visibility = View.VISIBLE
                            }

                            lastVisibility = binding.minMaxProgress.visibility
                        }
                    }
                }
        )

        date?.let {
            viewModel.getMinMax(it).observe(this, Observer {
                it?.let {
                    it.points.let { setChartData(it) }
                    it.max?.let {
                        binding.minMaxChart.axisLeft.axisMaximum = it
                    }
                    binding.minMaxChart.invalidate()
                }
            })
        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            resetChart()
            binding.minMaxProgress.show()
        } else {
            binding.minMaxProgress.hide()
            if (binding.minMaxProgress.visibility == View.GONE) {
                binding.minMaxContent.visibility = View.VISIBLE
            }
        }
    }

    private fun resetChart() {
        binding.minMaxContent.visibility = View.INVISIBLE
        binding.minMaxChart.data?.allData?.forEach { it.clearValues() }
        binding.minMaxChart.data?.clearValues()
    }

    private fun setChartData(it: List<MinMaxDataPoint>) {
        binding.minMaxChart.apply {
            xAxis.valueFormatter = viewModel.createMinMaxAxisFormatter(it)
            xAxis.axisMaximum = it.size.minus(1).toFloat()
            xAxis.labelCount = it.size

            val usageSet = LineDataSet(
                    it.mapIndexed { index, minMaxDataPoint ->
                        minMaxDataPoint.usage?.let {
                            Entry(index.toFloat(), it)
                        }
                    }.filterNotNull(),
                    getString(R.string.consumption)
            ).apply {
                color = ContextCompat
                        .getColor(context, R.color.consumption_color)
                setDrawCircles(false)
                setDrawValues(false)
                setDrawFilled(true)
                fillColor = ContextCompat
                        .getColor(context, R.color.consumption_color)
            }

            val minSet = ScatterDataSet(
                    it.mapIndexed { index, minMaxDataPoint ->
                        minMaxDataPoint.usage?.let {
                            if (minMaxDataPoint.minimum != null) {
                                Entry(index.toFloat(), it)
                            } else {
                                null
                            }
                        }
                    }.filterNotNull(),
                    getString(R.string.consumed_min)
            ).apply {
                color = ContextCompat
                        .getColor(context, R.color.min_max_min_color)
                setDrawValues(false)
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                scatterShapeSize = 24f
            }

            val maxSet = ScatterDataSet(
                    it.mapIndexed { index, minMaxDataPoint ->
                        minMaxDataPoint.usage?.let {
                            if (minMaxDataPoint.maximum != null) {
                                Entry(index.toFloat(), it)
                            } else {
                                null
                            }
                        }
                    }.filterNotNull(),
                    getString(R.string.consumed_max)
            ).apply {
                color = ContextCompat
                        .getColor(context, R.color.min_max_max_color)
                setDrawValues(false)
                setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                scatterShapeSize = 24f
            }

            data = CombinedData().apply {
                setData(
                        LineData().apply {
                            if (usageSet.values.size > 0) {
                                addDataSet(usageSet)
                            }
                        }
                )
                setData(
                        ScatterData().apply {
                            if (minSet.values.size > 0) {
                                addDataSet(minSet)
                            }

                            if (maxSet.values.size > 0) {
                                addDataSet(maxSet)
                            }
                        }
                )
            }
        }
    }

    private fun CombinedChart.styleChart() {
        description.isEnabled = false
        axisRight.isEnabled = false
        axisLeft.axisMinimum = 0.0f
        xAxis.axisMinimum = 0.0f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        axisLeft.textColor = ContextCompat.getColor(context, R.color.axis_value_text_color)
        xAxis.textColor = ContextCompat.getColor(context, R.color.axis_value_text_color)
        axisLeft.enableGridDashedLine(10.0f, 5.0f, 10.0f)
        axisLeft.setDrawGridLines(true)
        axisLeft.setDrawAxisLine(false)
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        setScaleEnabled(false)
        setTouchEnabled(false)

        legend.setCustom(
                mutableListOf(
                        LegendEntry().apply {
                            formColor = ContextCompat.getColor(context, R.color.min_max_min_color)
                            form = Legend.LegendForm.CIRCLE
                            formSize = 12f
                            label = getString(R.string.consumed_min)
                        },
                        LegendEntry().apply {
                            formColor = ContextCompat.getColor(context, R.color.min_max_max_color)
                            form = Legend.LegendForm.CIRCLE
                            formSize = 12f
                            label = getString(R.string.consumed_max)
                        }
                )
        )
    }
}

