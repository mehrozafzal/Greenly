package greenely.greenely.history.views

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Canvas
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryConsumptionChartFragmentBinding
import greenely.greenely.feed.utils.charting.CustomCombinedChartRenderer
import greenely.greenely.feed.utils.charting.CustomYAxisRenderer
import greenely.greenely.history.Consumption
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.models.HeaderModel
import greenely.greenely.utils.toCurrenyWithDecimalCheckAndSymbol
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment for showing a single consumption chart.
 */
class ConsumptionChartFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a [ConsumptionChartFragment] for a specified [date].
         */
        fun create(date: DateTime, headerModel: HeaderModel): ConsumptionChartFragment =
                ConsumptionChartFragment().apply {
                    this.date = date
                    this.headerModel = headerModel
                }
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: HistoryConsumptionChartFragmentBinding

    private lateinit var viewModel: HistoryViewModel

    private lateinit var headerModel: HeaderModel

    private var date: DateTime? = null

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_consumption_chart_fragment, container, false)

        return binding.root
    }

    /**
     * Bind to view model.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.headerModel = headerModel
        binding.executePendingBindings()

        viewModel.isConsumptionLoading()
                .observe(this, Observer { it?.let { setLoading(it) } })

        binding.consumptionProgress.viewTreeObserver
                .addOnGlobalLayoutListener(getLoaderVisibilityHandler())

        date?.let {
            viewModel.getConsumption(it).observe(this, Observer {
                it?.let {
                    setConsumption(it.points)
                    it.max?.let {
                        binding.consumptionChart.axisLeft.axisMaximum = it
                    }

                    binding.consumptionChart.invalidate()
                }
            })
        }




        styleChart()
    }

    /**
     * Load the data.
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
        binding.consumptionChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisRight.isEnabled = false
            axisLeft.setDrawAxisLine(false)
//            axisLeft.enableGridDashedLine(10.0f, 5.0f, 10.0f)
            axisLeft.textColor = ContextCompat.getColor(context, R.color.grey21)
            xAxis.textColor = ContextCompat.getColor(context, R.color.grey21)
            axisLeft.gridColor = ContextCompat.getColor(context, R.color.grey9)
            axisLeft.setLabelCount(3, true)
            axisLeft.gridLineWidth = 0.5f

            xAxis.axisMinimum = -1f
//            xAxis.setLabelCount(7,true)

            xAxis.setDrawAxisLine(false)
            description.isEnabled = false
            axisLeft.axisMinimum = 0.0f
            setTouchEnabled(true)
            legend.isEnabled = false
            setScaleEnabled(false)
//            xAxis.setAvoidFirstLastClipping(true)

            rendererLeftYAxis = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(axisLeft.axisDependency))
            renderer = CustomCombinedChartRenderer(this, animator, viewPortHandler)
            this.drawOrder = listOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE).toTypedArray()
            marker = CustomMarkerView(context, R.layout.history_total_chart_view_marker, this)


        }
    }

    private fun getLoaderVisibilityHandler(): ViewTreeObserver.OnGlobalLayoutListener {
        return object : ViewTreeObserver.OnGlobalLayoutListener {
            private var lastVisibility: Int = View.GONE
            /**
             * Update chart visibility based on loaders visibility
             */
            override fun onGlobalLayout() {
                if (lastVisibility != binding.consumptionProgress.visibility) {
                    if (binding.consumptionProgress.visibility == View.VISIBLE) {
                        binding.consumptionContent.visibility = View.INVISIBLE
                    } else {
                        binding.consumptionContent.visibility = View.VISIBLE
                    }
                }
                lastVisibility = binding.consumptionProgress.visibility
            }
        }
    }

    private fun setConsumption(consumption: List<Consumption>) {
        binding.consumptionChart.xAxis.valueFormatter = viewModel
                .createConsumptionAxisFormatter(consumption)

        var combinedData = CombinedData()


        var barData = BarData(
                BarDataSet(
                        consumption.mapIndexed { index, cons ->
                            BarEntry(index.toFloat(), cons.usage ?: 0.0f)
                        },
                        getString(R.string.consumption)
                ).apply {
                    setDrawValues(false)
                    isHighlightEnabled = true
                    highLightColor = ContextCompat.getColor(context!!, R.color.lime_green)
                    color = ContextCompat.getColor(context!!, R.color.green_1)
                }
        )

        combinedData.setData(barData)
        binding.consumptionChart.data = combinedData
        binding.consumptionChart.xAxis.axisMaximum = consumption.size.toFloat()
//         binding.consumptionChart.xAxis.setLabelCount(5,true)

        binding.consumptionChart.animateY(500)

    }


    private fun setLoading(loading: Boolean) {
        if (loading) {
            resetConsumptionChart()
            binding.consumptionProgress.show()
        } else {
            binding.consumptionProgress.hide()
            if (binding.consumptionProgress.visibility == View.GONE) {
                binding.consumptionContent.visibility = View.VISIBLE
            }
        }
    }

    private fun resetConsumptionChart() {
        binding.consumptionContent.visibility = View.INVISIBLE
        binding.consumptionChart.data?.clearValues()
    }


    class CustomMarkerView(val mContext: Context, layoutResource: Int, val chart: CombinedChart) : MarkerView(mContext, layoutResource) {

        private val tvMe: TextView


        init {
            // this markerview only displays a textview
            tvMe = findViewById<TextView>(R.id.tvMe)
            chartView = chart

        }

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            e?.let {
                
                tvMe.text = e.y.toCurrenyWithDecimalCheckAndSymbol()
                tvMe.visibility = if (e.y > 0f) View.VISIBLE else View.GONE
                super.refreshContent(e, highlight)

            }

        }

        override fun draw(canvas: Canvas, posX: Float, posY: Float) {

            val offset = getOffsetForDrawingAtPoint(posX, posY)

            val saveId = canvas.save()
            var viewWidth = (mContext.resources.getDimension(R.dimen.marker_view_width)) / 2
            // translate to the correct position and draw
            if (posX < viewWidth)
                canvas.translate((posX - viewWidth / 2) + offset.x / 2, 0f)
            else
                canvas.translate((posX - viewWidth) + offset.x / 2, 0f)

            draw(canvas)
            canvas.restoreToCount(saveId)
        }

    }

}


