package greenely.greenely.feed.models.feeditems

import greenely.greenely.feed.models.ChartData
import org.joda.time.DateTime

data class WeeklyReport(
        override val date: DateTime,
        val currentPeriodTitle: String,
        val body: CharSequence,
        var isNewEntry: Boolean,
        val currentPeriodChartData: ChartData?,
        val previousPeriodChartData: ChartData?,
        val currentPeriodTotalStr: String,
        val previousPeriodTotalStr: String?,
        var previousPeriodTitle: String?="--"
        ) : DatedFeedItem {
    override fun read() = this.copy(isNewEntry = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeeklyReport

        if (date != other.date) return false
        if (currentPeriodTitle != other.currentPeriodTitle) return false
        if (body != other.body) return false
        if (isNewEntry != other.isNewEntry) return false
        if (currentPeriodTotalStr != other.currentPeriodTotalStr) return false
        if (previousPeriodTotalStr != other.previousPeriodTotalStr) return false
        if (previousPeriodTitle != other.previousPeriodTitle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + currentPeriodTitle.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + isNewEntry.hashCode()
        result = 31 * result + (currentPeriodChartData?.hashCode() ?: 0)
        result = 31 * result + (previousPeriodChartData?.hashCode() ?: 0)
        result = 31 * result + currentPeriodTotalStr.hashCode()
        result = 31 * result + (previousPeriodTotalStr?.hashCode() ?: 0)
        result = 31 * result + (previousPeriodTitle?.hashCode() ?: 0)
        return result
    }


}

