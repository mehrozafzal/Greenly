package greenely.greenely.feed.mappers

import greenely.greenely.feed.models.CostAnalysisDataPoint
import greenely.greenely.feed.models.MonthDataPoint
import greenely.greenely.feed.models.MonthlyDataPointJson
import greenely.greenely.feed.models.json.CostAnalysisJson
import org.joda.time.DateTime
import javax.inject.Inject

class CostAnalysisDataPointMapper @Inject constructor() {

    fun fromJson(costAnalysisChartData: CostAnalysisJson.ChartData): CostAnalysisDataPoint {

        return CostAnalysisDataPoint(DateTime(costAnalysisChartData.timestamp * 1000L),
                costAnalysisChartData.usage,costAnalysisChartData.greenelyPrice,costAnalysisChartData.marketPrice)
    }
}