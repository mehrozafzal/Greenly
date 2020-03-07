package greenely.greenely.home.ui.viewholders

import greenely.greenely.databinding.HistoricalComparisonFragmentBinding
import greenely.greenely.history.views.HolderModel
import greenely.greenely.home.models.HistoricalComparisonModel
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.ui.historicalcomparison.HistoricalComparisonChartSetupFactory
import vn.tiki.noadapter2.AbsViewHolder

class HistoricalComparisonViewHolder(val binding: HistoricalComparisonFragmentBinding,
                                     val historicalComparisonSetupFactory: HistoricalComparisonChartSetupFactory
                                     ) : AbsViewHolder(binding.root) {


    override fun bind(item: Any?) {
        super.bind(item)
        if (item is HolderModel.HistoricalComparisonHolder) {

            binding.homeModel=item.homeModel
            binding.executePendingBindings()
            item?.let { historicalComparison ->
                historicalComparisonSetupFactory.createChartSetupWithData(
                        item.historicalComparisonModel.points,
                        item.homeModel.resolution ?: -1,
                        item.historicalComparisonModel.pointsMaxValue
                ).applyToChart(binding.chart)
            }


        }
    }
}