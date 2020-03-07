package greenely.greenely.feed.mappers

import greenely.greenely.feed.models.DataPoint
import greenely.greenely.feed.models.MonthDataPoint
import greenely.greenely.feed.models.MonthlyDataPointJson
import greenely.greenely.feed.models.json.DataPointJson
import greenely.greenely.feed.models.json.MonthlyReportJson
import org.joda.time.DateTime
import javax.inject.Inject

class MonthlyPointMapper @Inject constructor() {

    fun fromJson(monthlyPointJson: MonthlyDataPointJson, monthlyDataHelper: MonthlyDataHelper): MonthDataPoint {

        return MonthDataPoint(
                DateTime(monthlyPointJson.timestamp * 1000L),
                monthlyPointJson.usage, processPoint(monthlyPointJson.temperature,monthlyDataHelper.minTemperature, monthlyDataHelper.maxTemperature, monthlyDataHelper.offset),
                processPoint(monthlyPointJson.poolSpotPrice,monthlyDataHelper.minSpotPrice, monthlyDataHelper.maxSpotPrice, monthlyDataHelper.offset)
        )
    }


    fun mapIgnoreTemperature(monthlyPointJson:MonthlyDataPointJson, monthlyDataHelper: MonthlyDataHelper): MonthDataPoint {

        return MonthDataPoint(
                DateTime(monthlyPointJson.timestamp * 1000L),
                monthlyPointJson.usage, 0f,
                processPoint(monthlyPointJson.poolSpotPrice,monthlyDataHelper.minSpotPrice, monthlyDataHelper.maxSpotPrice, monthlyDataHelper.offset)
        )
    }

    fun mapIgnoreSpot(monthlyPointJson:MonthlyDataPointJson, monthlyDataHelper: MonthlyDataHelper): MonthDataPoint {

        return MonthDataPoint(
                DateTime(monthlyPointJson.timestamp * 1000L),
                monthlyPointJson.usage, processPoint(monthlyPointJson.temperature,monthlyDataHelper.minTemperature, monthlyDataHelper.maxTemperature, monthlyDataHelper.offset),
                0f
        )
    }

    fun mapIgnoreUsage(monthlyPointJson:MonthlyDataPointJson): MonthDataPoint {

        return MonthDataPoint(
                DateTime(monthlyPointJson.timestamp * 1000L),
                0, monthlyPointJson.temperature?.toFloat(),
                monthlyPointJson.poolSpotPrice?.toFloat()
        )
    }

    fun processPoint(point: Int?, min: Int, max: Int, maxConsumption: Int): Float? {

        point?.let {
            var nor: Float = ((point.toFloat()-min) / (max-min))
            var scaling = maxConsumption * nor
            var offSet = maxConsumption + scaling
            return offSet
        }
        return null

    }

    data class MonthlyDataHelper(val minSpotPrice: Int, val maxSpotPrice: Int,
                                 val minTemperature: Int, val maxTemperature: Int,
                                 val offset: Int)
}

