package greenely.greenely.home.ui.viewholders

import greenely.greenely.databinding.LatestComparisonChartBinding
import greenely.greenely.history.views.HolderModel
import greenely.greenely.home.ui.latestcomparison.LatestComparisonChartSetupFactory
import vn.tiki.noadapter2.AbsViewHolder

class LatestComparisonViewHolder(val binding: LatestComparisonChartBinding,
                                 val latestComparisonSetupFactory: LatestComparisonChartSetupFactory
                                ) : AbsViewHolder(binding.root) {

    override fun bind(item: Any?) {
        super.bind(item)


        if (item is HolderModel.LatestComparisonHolder) {
            binding.homeModel=item.homeModel
            binding.executePendingBindings()

            if(item.homeModel.isWaitingState())
                binding.waitingContainer.rippleContainer.startRippleAnimation()
            else if ( item.homeModel.isWaitingForZavanneState())
                binding.waitingZavanneContainer.rippleContainer.startRippleAnimation()


            item?.let {
                latestComparisonSetupFactory.createChartSetupWithData(
                        item.latestComparisonModel.comparison,
                        item.latestComparisonModel.maxComparison
                ).applyToChart(binding.chart)

            }
        }

    }
}