package greenely.greenely.feed.models

import org.joda.time.DateTime

 class MonthDataPoint (date: DateTime,
                            usage: Int?,
                          val temperature:Float?=null,
                           val spotPrice:Float?=null) : DataPoint(date,usage) {

     fun temperaturePointsAsPair(): Pair<DateTime, Float?> = date to (temperature)

     fun spotPricePointsAsPair(): Pair<DateTime, Float?> = date to (spotPrice)
}