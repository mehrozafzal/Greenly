package greenely.greenely.home.ui

import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import greenely.greenely.databinding.HistoricalComparisonFragmentBinding
import greenely.greenely.databinding.LatestComparisonChartBinding
import greenely.greenely.home.models.HomeModel
import greenely.greenely.utils.StringUtils

class GraphAdapter(
        private val fragment: HomeFragment,
        private val model: HomeModel? = null
)  {

//    companion object {
//        @JvmStatic
//        fun create(fragment: HomeFragment, model: HomeModel): androidx.viewpager.widget.PagerAdapter {
//            return GraphAdapter(fragment, model)
//        }
//    }
//
//    override fun getCount(): Int = 2
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }
//
//
//    override fun instantiateItem(container: ViewGroup, position: Int): View {
//        val linearLayout: ViewDataBinding
//
//        when (position) {
//            0 -> {
//                linearLayout = LatestComparisonChartBinding.inflate(
//                        LayoutInflater.from(container.context), container, false
//                )
//                linearLayout.title.text = StringUtils.upperFirst(model?.latestComparisonModel?.title)
//                model?.latestComparisonModel?.title?.let {
//                    linearLayout.subheading.visibility = View.VISIBLE
//                    linearLayout.subheading.text = model.latestComparisonModel?.title
//                }
//
//                model?.latestComparisonModel?.let { latestComparison ->
//                    fragment.latestComparisonSetupFactory.createChartSetupWithData(
//                            latestComparison.comparison,
//                            model.comparisonMaxValue
//                    ).applyToChart(linearLayout.chart)
//                }
//            }
//            1 -> {
//                linearLayout = HistoricalComparisonFragmentBinding.inflate(
//                        LayoutInflater.from(container.context), container, false
//                )
//
//                model?.historicalComparisonModel?.let { historicalComparison ->
//                    fragment.historicalComparisonSetupFactory.createChartSetupWithData(
//                            historicalComparison.points,
//                            model.resolution ?: -1,
//                            model.pointsMaxValue
//                    ).applyToChart(linearLayout.chart)
//                }
//                model?.historicalComparisonModel?.title?.let {
//                    linearLayout.title.text = container.context.getString(it)
//                }
//            }
//
//            else -> throw IndexOutOfBoundsException("No step for $position")
//        }
//
//        container.addView(linearLayout.root)
//        return linearLayout.root
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
//        if (obj is View) container.removeView(obj)
//        else container.removeAllViews()
//    }

}