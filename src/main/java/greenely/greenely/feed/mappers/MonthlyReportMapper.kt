package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.ChartType
import greenely.greenely.feed.models.MonthlyChartData
import greenely.greenely.feed.models.MonthlyDataPointJson
import greenely.greenely.feed.models.feeditems.MonthlyReport
import greenely.greenely.feed.models.json.MonthlyReportJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class MonthlyReportMapper @Inject constructor(
        private val monthlyPointMapper: MonthlyPointMapper,
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(monthlyReportJson: MonthlyReportJson): MonthlyReport {
        val date = DateTime(monthlyReportJson.timestamp * 1000L)


        var validSpotList = monthlyReportJson.chartData?.filterNot { P -> P.poolSpotPrice == null }
        var validTempList = monthlyReportJson.chartData?.filterNot { P -> P.temperature == null }
        var validUsageList = monthlyReportJson.chartData?.filterNot { P -> P.usage == null }

        var minSpotPrice: MonthlyDataPointJson? = validSpotList?.minBy { it.poolSpotPrice!! }
        var maxSpotPrice: MonthlyDataPointJson? = validSpotList?.maxBy { it.poolSpotPrice!! }
        var minTemperature: MonthlyDataPointJson? = validTempList?.minBy { it.temperature!! }
        var maxTemperature: MonthlyDataPointJson? = validTempList?.maxBy { it.temperature!! }

        var offset: MonthlyDataPointJson? = validUsageList?.maxBy { it.usage!! }


        var monthlyChartData: MonthlyChartData?
        if (maxTemperature != null && maxSpotPrice != null && offset != null) {
            monthlyChartData = monthlyReportJson.chartData?.let {
                MonthlyChartData(it.map {
                    monthlyPointMapper.fromJson(it, MonthlyPointMapper.MonthlyDataHelper(
                            minSpotPrice?.poolSpotPrice!!, maxSpotPrice?.poolSpotPrice!!,
                            minTemperature?.temperature!!, maxTemperature?.temperature!!, offset?.usage!!))
                })

            }
        } else if (maxTemperature == null && maxSpotPrice != null && offset != null) {
            monthlyChartData = monthlyReportJson.chartData?.let {
                MonthlyChartData(it.map {
                    monthlyPointMapper.mapIgnoreTemperature(it, MonthlyPointMapper.MonthlyDataHelper(minSpotPrice?.poolSpotPrice!!, maxSpotPrice.poolSpotPrice!!,
                            0, 0, offset.usage!!))
                })
            }
        } else if (maxTemperature != null && maxSpotPrice == null && offset != null) {
            monthlyChartData = monthlyReportJson.chartData?.let {
                MonthlyChartData(it.map {
                    monthlyPointMapper.mapIgnoreSpot(it, MonthlyPointMapper.MonthlyDataHelper(0, 0,
                            minTemperature?.temperature!!, maxTemperature.temperature!!, offset.usage!!))
                })
            }
        } else {
            monthlyChartData = monthlyReportJson.chartData?.let {
                MonthlyChartData(it.map { monthlyPointMapper.mapIgnoreUsage(it) })
            }
        }



        return MonthlyReport(
                date,
                monthlyReportJson.title,
                feedItemBodyFormatter.formatBody(monthlyReportJson.body),
                monthlyReportJson.isNewEntry,
                monthlyChartData
        )
    }
}

