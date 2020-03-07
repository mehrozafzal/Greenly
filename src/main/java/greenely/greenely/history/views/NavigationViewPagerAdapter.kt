package greenely.greenely.history.views

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.StatefulAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.NavigationChartFragmentBinding
import greenely.greenely.history.HistoryResolution
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.NavigationData
import greenely.greenely.history.NavigationDataPoint
import greenely.greenely.history.views.chart.CustomBarChartRender
import greenely.greenely.retail.ui.RetailComingSoonFourthFragment
import greenely.greenely.retail.ui.RetailComingSoonFragment
import greenely.greenely.retail.ui.RetailSecondFragment
import greenely.greenely.retail.ui.RetailThirdFragment
import kotlinx.android.synthetic.main.history_fragment.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject


class NavigationChartFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun create(navigationData: NavigationData) = NavigationChartFragment().apply {
            this.max = navigationData.max
            this.data = navigationData.data
        }
    }

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: NavigationChartFragmentBinding

    private var data: List<NavigationDataPoint>? = null
    private var max: Float? = null

    private var selectedEntry: BarEntry? = null

    private lateinit var customXAxisRenderer: CustomXAxisRenderer

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.navigation_chart_fragment, container, false)
        return binding.root
    }

    private var shouldUpdateSelectedDate: Boolean = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders
                .of(activity!!, viewModelFactory)
                .get(HistoryViewModel::class.java)

        binding.navigationChart.let { view ->
            max?.let {
                view.axisLeft.axisMaximum = it*1.2f
            }
        }

        binding.navigationChart.let { view ->
            setUpChartStyle(view)
            setChartData(view)
            view.animateY(500)
            view.setOnChartValueSelectedListener(getChartValueSelectListener(view))

            viewModel.getSelectedDataPoint().observe(this, Observer {
                it?.let { setSelectedDate(it.dateTime, view) }
            })
        }
    }

    private fun setSelectedDate(selectedDate: DateTime, view: BarChart) {
        data?.let { data ->
            shouldUpdateSelectedDate = false
            val indexOfValue = data.indexOfFirst { it.dateTime == selectedDate }
            if (indexOfValue != -1) {
                view.highlightValue(indexOfValue.toFloat(), 0)
                view.highlighted?.let {
                     selectedEntry=BarEntry( it.first().x,it.first().y)
                    customXAxisRenderer.selectedEntry=selectedEntry
                }
            } else {
                view.highlightValue(null)
                selectedEntry=null
                customXAxisRenderer.selectedEntry=selectedEntry

            }
            shouldUpdateSelectedDate = true

            view.invalidate()
        }
    }

    private fun getChartValueSelectListener(view: BarChart): OnChartValueSelectedListener {
        return object : OnChartValueSelectedListener {

            private var lastEntry: Entry? = null

            override fun onNothingSelected() {
                lastEntry?.let {
                    view.highlightValue(it.x, 0)
                }
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null && shouldUpdateSelectedDate) {
                    selectedEntry = e as BarEntry
                    customXAxisRenderer.selectedEntry = selectedEntry
                    data?.let {
                        viewModel.selectDataPoint(it[e.x.toInt()])
                        if (!it[e.x.toInt()].selectable) {
                            lastEntry?.let {
                                view.highlightValue(it.x, 0)
                            }
                        } else {
                            lastEntry = e
                        }
                    }
                }
            }
        }
    }

    private fun setChartData(view: BarChart) {
        data?.let { data ->
            view.xAxis.valueFormatter = viewModel.createNavigationAxisFormatter(data)
            view.xAxis.labelCount = data.size

            val minBarHeight = view.axisLeft.axisMaximum * 0.1f
            val unClickable = data.filter { !it.selectable }
            val clickable = data.filter { it.selectable }
            view.data = BarData(
                    BarDataSet(
                            clickable.map { it ->
                                BarEntry(
                                        data.indexOf(it).toFloat(),
                                        it.usage?.plus(minBarHeight) ?: minBarHeight
                                )
                            },
                            getString(R.string.consumption)
                    ).apply {
                        setDrawValues(false)
                        axisDependency = YAxis.AxisDependency.LEFT
                        color = ContextCompat.getColor(context!!, R.color.history_green_bar_color)
                        highLightAlpha = 255
                        highLightColor = ContextCompat
                                .getColor(context!!, R.color.white)
                    },
                    BarDataSet(
                            unClickable.map {
                                BarEntry(
                                        data.indexOf(it).toFloat(),
                                        it.usage?.plus(0f) ?: 0f
                                )
                            },
                            getString(R.string.consumption)
                    ).apply {
                        setDrawValues(false)
                        axisDependency = YAxis.AxisDependency.LEFT
                        isHighlightEnabled = false
                        color = ContextCompat.getColor(context!!, R.color.green_3)

                    }
            ).apply { barWidth = 0.98f }


        }
    }

    private fun setUpChartStyle(view: BarChart) {
        view.isHighlightPerDragEnabled = false
        view.axisLeft.isEnabled = false
        view.axisRight.isEnabled = false
        view.xAxis.setDrawGridLines(false)
        view.axisLeft.axisMinimum = 0.0f
        view.setScaleMinima(0.0f, 0.0f)
        view.legend.isEnabled = false
        view.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
        }


        view.setScaleEnabled(false)
        view.setViewPortOffsets(0f, 0f, 0f, 0f)
        view.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE;
        context?.let {
            view.xAxis.textColor = ContextCompat.getColor(it, R.color.white)
        }


        view.renderer = CustomBarChartRender(view, view.animator, view.viewPortHandler)
        customXAxisRenderer = CustomXAxisRenderer(view, view.viewPortHandler, view.xAxis, view.getTransformer(view.axisLeft.axisDependency))
        view.setXAxisRenderer(customXAxisRenderer)
        view.description.isEnabled = false
    }


    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}


/**
 * Adapter for displaying the navigation charts.
 */
class NavigationViewPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    var data: List<NavigationData> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): androidx.fragment.app.Fragment =
            NavigationChartFragment.create(data[position])

    override fun getCount(): Int = data.size

    fun getSelectDataPointIndex(navigationDataPoint: NavigationDataPoint?): Int {
        data.forEach {
            if (it.data.contains(navigationDataPoint)) return data.indexOf(it)
        }
        return -1
    }

    override fun saveState(): Parcelable? {
        // Do not save state.
        return null
    }
}

class CustomXAxisRenderer(val chart: BarChart, viewPortHandler: ViewPortHandler, xAxis: XAxis, trans: Transformer) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    var selectedEntry: BarEntry? = null

    override fun drawLabels(c: Canvas, pos: Float, anchor: MPPointF) {
        val labelRotationAngleDegrees = mXAxis.labelRotationAngle
        val centeringEnabled = mXAxis.isCenterAxisLabelsEnabled

        val positions = FloatArray(mXAxis.mEntryCount * 2)

        run {
            var i = 0
            while (i < positions.size) {

                // only fill x values
                if (centeringEnabled) {
                    positions[i] = mXAxis.mCenteredEntries[i / 2]
                } else {
                    positions[i] = mXAxis.mEntries[i / 2]
                }
                i += 2
            }
        }

        mTrans.pointValuesToPixel(positions)

        var i = 0
        while (i < positions.size) {

            var x = positions[i]


            if (mViewPortHandler.isInBoundsX(x)) {

                val label = mXAxis.valueFormatter.getFormattedValue(mXAxis.mEntries[i / 2], mXAxis)

                if (mXAxis.isAvoidFirstLastClippingEnabled) {

                    // avoid clipping of the last
                    if (i == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()

                        if (width > mViewPortHandler.offsetRight() * 2 && x + width > mViewPortHandler.chartWidth)
                            x -= width / 2

                        // avoid clipping of the first
                    } else if (i == 0) {

                        val width = Utils.calcTextWidth(mAxisLabelPaint, label).toFloat()
                        x += width / 2
                    }
                }

                var ypos=pos-35f

                if (selectedEntry != null) {
                    selectedEntry?.let {

                        val pix = chart.getTransformer(chart.axisLeft.axisDependency).getPixelForValues(it.x, it.y * chart.animator.phaseY)
                        if (pix.x == x.toDouble()) {
                            drawLabelInverted(c, label, x, ypos, anchor, labelRotationAngleDegrees)
                        } else
                        {
                            mAxisLabelPaint.color=Color.parseColor("#ffffff")
                            drawLabel(c, label, x, ypos, anchor, labelRotationAngleDegrees)
                        }


                    }
                } else
                {
                    drawLabel(c, label, x, ypos, anchor, labelRotationAngleDegrees)

                }


            }
            i += 2
        }
    }

    protected fun drawLabelInverted(c: Canvas, formattedLabel: String, x: Float, y: Float, anchor: MPPointF, angleDegrees: Float) {
        var invertedPaint = mAxisLabelPaint
        invertedPaint.color = Color.parseColor("#222222")
        Utils.drawXAxisValue(c, formattedLabel, x, y, invertedPaint, anchor, angleDegrees)
    }
}

