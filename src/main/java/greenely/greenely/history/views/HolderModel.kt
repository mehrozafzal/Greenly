package greenely.greenely.history.views

import greenely.greenely.home.models.HistoricalComparisonModel
import greenely.greenely.home.models.HomeModel
import greenely.greenely.home.models.LatestComparisonModel

sealed class HolderModel {
    class LatestComparisonHolder(val homeModel: HomeModel,val latestComparisonModel: LatestComparisonModel) : HolderModel()
    class HistoricalComparisonHolder(val homeModel: HomeModel,val historicalComparisonModel: HistoricalComparisonModel) : HolderModel()
}