package greenely.greenely.feed.models.feeditems

import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.MonthlyChartData
import org.joda.time.DateTime

data class MonthlyReport(
        override val date: DateTime,
        val title: String,
        val body: CharSequence,
        var isNewEntry: Boolean,
        val chartData: MonthlyChartData?
) : DatedFeedItem {
    override fun read() = this.copy(isNewEntry = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonthlyReport

        if (date != other.date) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (isNewEntry != other.isNewEntry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + body.hashCode()
        result = 31 * result + isNewEntry.hashCode()
        return result
    }


}

