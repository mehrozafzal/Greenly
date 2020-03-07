package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.feeditems.WeeklyReport
import greenely.greenely.feed.models.json.DataPointJson
import greenely.greenely.feed.models.json.WeeklyReportJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class WeeklyReportMapper @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter,
        private val dataPointMapper: DataPointMapper
) {
    fun fromJson(weeklyReportJson: WeeklyReportJson): WeeklyReport {
        val date = DateTime(weeklyReportJson.timestamp * 1000L)

        val currentPeriodChatData = weeklyReportJson.currentPeriodChartData?.let { dataPointList: List<DataPointJson> ->
            ChartData(dataPointList.map { dataPointMapper.fromJson(it) })
        }

        val previousPeriodChatData = weeklyReportJson.previousPeriodChartData?.let { dataPointList: List<DataPointJson> ->
            ChartData(dataPointList.map { dataPointMapper.fromJson(it) })
        }


        return WeeklyReport(
                date,
                weeklyReportJson.title,
                feedItemBodyFormatter.formatBody(weeklyReportJson.body),
                weeklyReportJson.isNewEntry,
                currentPeriodChatData,
                previousPeriodChatData,
                weeklyReportJson.currentPeriodChartTotal.let { (it.div(1000).toCurrenyWithDecimalCheck())},
                weeklyReportJson.previousPeriodTotal?.let { (it?.div(1000)?.toCurrenyWithDecimalCheck())}
        )
    }
}

