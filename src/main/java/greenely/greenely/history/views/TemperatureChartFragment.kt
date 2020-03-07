package greenely.greenely.history.views

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryTemperatureChartFragmentBinding
import greenely.greenely.feed.utils.charting.CustomYAxisRenderer
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.Temperature
import greenely.greenely.history.models.HeaderModel
import greenely.greenely.history.views.chart.CustomCombinedChartRenderer
import greenely.greenely.home.ui.historicalcomparison.SetChartData
import greenely.greenely.utils.consumptionToString
import greenely.greenely.utils.toCurrenyWithDecimalCheckAndSymbol
import kotlinx.android.synthetic.main.history_temperature_chart_fragment.view.*
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment for displaying the temperature chart in history.
 */
class TemperatureChartFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a [TemperatureChartFragment] for a specified [date].
         */
        fun create(date: DateTime,headerModel: HeaderModel): TemperatureChartFragment = TemperatureChartFragment().apply {
            this.date = date
            this.headerModel=headerModel
        }
    }

    private var date: DateTime? = null

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HistoryViewModel

    private lateinit var headerModel: HeaderModel


    private lateinit var binding: HistoryTemperatureChartFragmentBinding

    /**
     * Create the binding and the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_temperature_chart_fragment, container, false)
        return binding.root
    }

    /**
     * Create the view model and bind to it.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.temperatureProgress.viewTreeObserver
                .addOnGlobalLayoutListener(getLoaderVisibilityHandler())
        viewModel.isTemperatureLoading().observe(this, Observer { it?.let { setLoading(it) } })

        binding.headerModel = headerModel
        binding.executePendingBindings()

        date?.let {
            viewModel.getTemperature(it).observe(this, Observer {
                it?.points?.let { setTemperatureData(it) }
                binding.temperatureChart.axisLeft.axisMinimum=it.temperatureMin?:0f
                binding.temperatureChart.invalidate()
            })
        }

        styleChart()
    }

    /**
     * Fetch the data
     */
    override fun onResume() {
        super.onResume()
    }

//    private fun setData()
//    {
//        binding.textView.text=headerModel.temperatureHeaderText
//    }

    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun styleChart() {
        binding.temperatureChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            axisLeft.setDrawAxisLine(false)
            axisLeft.textColor = ContextCompat.getColor(context, R.color.grey21)
            xAxis.textColor = ContextCompat.getColor(context, R.color.grey21)
            axisLeft.gridColor = ContextCompat.getColor(context, R.color.grey9)
            axisLeft.setLabelCount(3, true)
            axisLeft.gridLineWidth = 0.5f
//            xAxis.setLabelCount(6, true)
            xAxis.setDrawAxisLine(false)
            description.isEnabled = false
            setTouchEnabled(true)
            legend.isEnabled = false
            setScaleEnabled(false)
            rendererLeftYAxis = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(axisLeft.axisDependency))
            renderer = CustomCombinedChartRenderer(this, animator, viewPortHandler)
            this.drawOrder = listOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE).toTypedArray()
            marker = CustomMarkerView(context, R.layout.history_temperature_chart_marker_view, this)

        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            resetTemperatureChart()
            binding.temperatureProgress.show()
        } else {
            binding.temperatureProgress.hide()
            if (binding.temperatureProgress.visibility == View.GONE) {
                binding.temperatureContent.visibility = View.VISIBLE
            }
        }
    }

    private fun resetTemperatureChart() {
        binding.temperatureContent.visibility = View.INVISIBLE
        binding.temperatureChart.data?.clearValues()
    }

    private fun getLoaderVisibilityHandler(): ViewTreeObserver.OnGlobalLayoutListener {
        return object : ViewTreeObserver.OnGlobalLayoutListener {
            private var lastVisibility = View.GONE
            /**
             * Update the content visibility based on the visibility of the loader.
             */
            override fun onGlobalLayout() {
                if (binding.temperatureProgress.visibility != lastVisibility) {
                    if (binding.temperatureProgress.visibility == View.VISIBLE) {
                        binding.temperatureContent.visibility = View.INVISIBLE
                    } else {
                        binding.temperatureContent.visibility = View.VISIBLE
                    }
                }
                lastVisibility = binding.temperatureProgress.visibility
            }

        }
    }

    private fun setTemperatureData(temperature: List<Temperature>) {
        binding.temperatureChart.xAxis.valueFormatter = viewModel
                .createTemperatureAxisFormatter(temperature)
        binding.temperatureChart.xAxis.axisMinimum = -1f
        binding.temperatureChart.legend.isEnabled = false
        binding.temperatureChart.axisLeft.axisMinimum = 0f
        binding.temperatureChart.axisRight.axisMinimum = 0f


        var maxUsage = temperature.maxBy { it.usage?.let { it } ?: 0f }?.usage ?: 0f
        var maxTemp = temperature.maxBy { it.temperature?.let { it } ?: 0f }?.temperature ?: 0f

        binding.temperatureChart.axisLeft.axisMaximum = maxTemp * 1.2f

        binding.temperatureChart.axisRight.axisMaximum =  maxUsage * 1.2f

        binding.temperatureChart.xAxis.axisMaximum = temperature.size.minus(1).toFloat()+1f


        val consumptionSet = BarDataSet(
                temperature.mapIndexed { index, v ->
                    v.usage?.let {
                        BarEntry(index.toFloat(), it)
                    }
                }.filterNotNull(),
                getString(R.string.consumption)
        ).apply {
            setDrawValues(false)
            color = ContextCompat.getColor(context!!, R.color.grey19)
            axisDependency = YAxis.AxisDependency.RIGHT
            isHighlightEnabled = false

        }


        var acc = mutableListOf<Entry>()
        context?.let {
            var i = 0
            temperature.forEach {
                it?.temperature?.let {
                    acc.add(Entry(i.toFloat(), it, TemperatureMarkerObject(it, temperature.get(i).usage, ContextCompat.getColor(context!!, R.color.green_1))))
                }
                i++
            }
        }


        val temperatureSet = LineDataSet(acc, getString(R.string.temperature).capitalize()).apply {
            setDrawValues(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(context!!, R.color.green_1)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 4.0f
            setDrawHorizontalHighlightIndicator(false)
            highlightLineWidth = 0.9f
            context?.let {
                highLightColor = ContextCompat.getColor(it, R.color.grey6)
            }

        }


        binding.temperatureChart.data = CombinedData().apply {
            if (consumptionSet.values.size > 0) {
                setData(BarData(consumptionSet))
            }

            if (temperatureSet.values.size > 0) {
                setData(LineData(temperatureSet))
            }
        }

        binding.temperatureChart.animateY(500)

    }

    class CustomMarkerView(context: Context, layoutResource: Int, val chart: CombinedChart) : MarkerView(context, layoutResource) {

        private val tvMe: TextView


        init {
            // this markerview only displays a textview
            tvMe = findViewById<TextView>(R.id.tvMe)
            chartView = chart

        }

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            e?.let {

                it.data?.let {
                    if (it is TemperatureMarkerObject) {

                        if(it.usage!=null) {
                            tvMe.text= it.temp.consumptionToString()+" °c | "+ it.usage.toCurrenyWithDecimalCheckAndSymbol()
                        }
                        else
                            tvMe.text= it.temp.consumptionToString()+" °c"

//                        tvMe.text =  e.y.toCurrenyWithDecimalCheckAndSymbol()
//                        tvMe.visibility = if (e.y > 0f) View.VISIBLE else View.GONE
                    }
                }

                super.refreshContent(e, highlight)

            }

        }

        override fun draw(canvas: Canvas, posX: Float, posY: Float) {

            val offset = getOffsetForDrawingAtPoint(posX, posY)

            val saveId = canvas.save()
            var viewWidth = (context?.resources?.getDimension(R.dimen.marker_view_width) ?: 0f) / 2
            // translate to the correct position and draw
            canvas.translate(posX - 70f + offset.x, 0f)
            draw(canvas)
            canvas.restoreToCount(saveId)
        }

    }

    data class TemperatureMarkerObject(val temp: Float, val usage: Float?, val highlightCircleColor: Int)
}

