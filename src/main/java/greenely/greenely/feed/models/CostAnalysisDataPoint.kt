package greenely.greenely.feed.models

import org.joda.time.DateTime

class CostAnalysisDataPoint(date: DateTime,
                            usage: Int?,
                            val greenelyPrice: Float? = null,
                            val marketPrice: Float? = null) : DataPoint(date, usage) {

    fun marketPricePointsAsPair(): Pair<DateTime, Float> = date to (marketPrice ?: 0f)

    fun greenelyPricePointsAsPair(): Pair<DateTime, Float> = date to (greenelyPrice ?: 0f)
}