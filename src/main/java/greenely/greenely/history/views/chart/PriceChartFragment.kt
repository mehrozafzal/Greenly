package greenely.greenely.history.views.chart

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
import greenely.greenely.databinding.HistoryPriceChartFragmentBinding
import greenely.greenely.feed.utils.charting.CustomYAxisRenderer
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.Price
import greenely.greenely.history.models.HeaderModel
import greenely.greenely.history.views.TemperatureChartFragment
import greenely.greenely.utils.consumptionToString
import greenely.greenely.utils.toCurrenyWithDecimalCheckAndSymbol
import org.joda.time.DateTime
import javax.inject.Inject

class PriceChartFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a [TemperatureChartFragment] for a specified [date].
         */
        fun create(date: DateTime, headerModel: HeaderModel): PriceChartFragment = PriceChartFragment().apply {
            this.date = date
            this.headerModel=headerModel
        }
    }

    private var date: DateTime? = null

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HistoryViewModel

    private lateinit var headerModel: HeaderModel


    private lateinit var binding: HistoryPriceChartFragmentBinding

    /**
     * Create the binding and the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_price_chart_fragment, container, false)
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

        binding.priceProgress.viewTreeObserver
                .addOnGlobalLayoutListener(getLoaderVisibilityHandler())
        viewModel.isPriceLoading().observe(this, Observer { it?.let { setLoading(it) } })

        binding.headerModel = headerModel
        binding.viewModel=viewModel
        binding.executePendingBindings()

        date?.let {
            viewModel.getPrice(it).observe(this, Observer {
                it?.points?.let { setPriceData(it) }
                it?.priceMin?.let { binding.priceChart.axisRight.axisMinimum = it }
                binding.priceChart.invalidate()
            })
        }

        styleChart()

        binding.snackbarContainer.snackbarAction.setOnClickListener {
            viewModel.navigateToRetail()
        }
    }

    /**
     * Fetch the data
     */
    override fun onResume() {
        super.onResume()
    }


    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun styleChart() {
        binding.priceChart.apply {
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
            setNoDataText("  ")
            rendererLeftYAxis = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(axisLeft.axisDependency))
            renderer = CustomCombinedChartRenderer(this, animator, viewPortHandler)
            this.drawOrder = listOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE).toTypedArray()
            marker = CustomMarkerView(context, R.layout.history_price_chart_marker_view, this)

        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            resetTemperatureChart()
            binding.priceProgress.show()
        } else {
            binding.priceProgress.hide()
            if (binding.priceProgress.visibility == View.GONE) {
                binding.priceContent.visibility = View.VISIBLE
            }
        }
    }

    private fun resetTemperatureChart() {
        binding.priceContent.visibility = View.INVISIBLE
        binding.priceChart.data?.clearValues()
    }

    private fun getLoaderVisibilityHandler(): ViewTreeObserver.OnGlobalLayoutListener {
        return object : ViewTreeObserver.OnGlobalLayoutListener {
            private var lastVisibility = View.GONE
            /**
             * Update the content visibility based on the visibility of the loader.
             */
            override fun onGlobalLayout() {
                if (binding.priceProgress.visibility != lastVisibility) {
                    if (binding.priceProgress.visibility == View.VISIBLE) {
                        binding.priceContent.visibility = View.INVISIBLE
                    } else {
                        binding.priceContent.visibility = View.VISIBLE
                    }
                }
                lastVisibility = binding.priceProgress.visibility
            }

        }
    }

    private fun setPriceData(price: List<Price>) {
        binding.priceChart.xAxis.valueFormatter = viewModel
                .createPriceAxisFormatter(price)
        binding.priceChart.xAxis.axisMinimum = -1f
        binding.priceChart.legend.isEnabled = false
        binding.priceChart.xAxis.axisMaximum = price.size.minus(1).toFloat()+1f
        binding.priceChart.axisLeft.axisMinimum = 0f

        var maxUsage = price.maxBy { it.usage?.let { it } ?: 0f }?.usage ?: 0f
        var maxPrice = price.maxBy { it.price?.let { it } ?: 0f }?.price ?: 0f

        binding.priceChart.axisLeft.axisMaximum =  maxPrice *1.2f

        binding.priceChart.axisRight.axisMaximum = maxUsage *1.2f


        val consumptionSet = BarDataSet(
                price.mapIndexed { index, v ->
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
            price.forEach {
                it?.price?.let {
                    acc.add(Entry(i.toFloat(), it, PriceMarkerObject(it, price.get(i).usage, ContextCompat.getColor(context!!, R.color.yellow1))))
                }
                i++
            }
        }


        val priceSet = LineDataSet(acc, getString(R.string.price_label).capitalize()).apply {
            setDrawValues(false)
            setDrawCircles(false)
            color = ContextCompat.getColor(context!!, R.color.yellow1)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 4.0f
            setDrawHorizontalHighlightIndicator(false)
            highlightLineWidth = 0.9f
            context?.let {
                highLightColor = ContextCompat.getColor(it, R.color.grey6)
            }

        }


        binding.priceChart.data = CombinedData().apply {
            if (consumptionSet.values.size > 0) {
                setData(BarData(consumptionSet))
            }

            if (priceSet.values.size > 0) {
                setData(LineData(priceSet))
            }
        }

        binding.priceChart.animateY(500)

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
                    if (it is PriceMarkerObject) {

                        if(it.usage!=null) {
                            tvMe.text= it.temp.consumptionToString()+" öre/kWh | "+ it.usage.toCurrenyWithDecimalCheckAndSymbol()
                        }
                        else
                            tvMe.text= it.temp.consumptionToString()+" öre/kWh"

//                        tvMe.text =  e.y.toCurrenyWithDecimalCheckAndSymbol()
                        tvMe.visibility = if (e.y > 0f) View.VISIBLE else View.GONE
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

    data class PriceMarkerObject(val temp: Float, val usage: Float?, val highlightCircleColor: Int)
}