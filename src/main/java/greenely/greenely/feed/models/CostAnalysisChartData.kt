package greenely.greenely.feed.models

import org.joda.time.DateTime

class CostAnalysisChartData (
        val points: List<CostAnalysisDataPoint>) : ChartData(points)
{
    val greenelyPriceData: List<Pair<DateTime, Float>>
        get() = points.map { it.greenelyPricePointsAsPair() }

    val marketPriceData: List<Pair<DateTime, Float>>
        get() = points.map { it.marketPricePointsAsPair() }
}