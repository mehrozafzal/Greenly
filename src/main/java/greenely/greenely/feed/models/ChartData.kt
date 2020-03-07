package greenely.greenely.feed.models

import org.joda.time.DateTime


open class ChartData(
        private val points: List<DataPoint>
) : List<DataPoint> by points {
    val data: List<Pair<DateTime, Int>>
        get() = points.map { it.asPair() }
    val sum: Int
        get() = points.fold(0) { acc: Int, point: DataPoint -> acc + (point.usage ?: 0) }
    val date: DateTime
        get() = points.first().date
    var type: ChartType?=null



}


enum class ChartType {
    TEMPERATURE,
    USAGE,
    SPOTPRICE
}
