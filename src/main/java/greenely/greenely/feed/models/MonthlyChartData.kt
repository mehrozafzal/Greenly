package greenely.greenely.feed.models

import org.joda.time.DateTime

class MonthlyChartData(
        val points: List<MonthDataPoint>) : ChartData(points) {

    var hasValidTemperatureData: Boolean = false

    var hasValidSpotPriceData: Boolean = false

    init {
        hasValidTemperatureData=temperatureData.any { P -> P.second?.let { it > 0 } ?: true }

        hasValidSpotPriceData=spotPriceData.any { P -> P.second?.let { it > 0 } ?: true }

//        val hasValidSpotPriceData by lazy {
//            spotPriceData.any { P -> P.second?.let { it > 0 } ?: true }
//        }
    }


    val temperatureData: List<Pair<DateTime, Float?>>
        get() = points.map { it.temperaturePointsAsPair() }


    val spotPriceData: List<Pair<DateTime, Float?>>
        get() = points.map { it.spotPricePointsAsPair() }


}