package greenely.greenely.feed.models.feeditems

import org.joda.time.DateTime

data class PredictedInvoiceItem(override val date: DateTime,
                                val contextDate: String,
                                val title: String,
                                val subtitle: CharSequence,
                                val description: String,
                                val predictionInKr: String,
                                var isNewEntry: Boolean
) : DatedFeedItem {
    override fun read(): DatedFeedItem=this.copy(isNewEntry = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PredictedInvoiceItem

        if (date != other.date) return false
        if (contextDate != other.contextDate) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (description != other.description) return false
        if (predictionInKr != other.predictionInKr) return false
        if (isNewEntry != other.isNewEntry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + contextDate.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + predictionInKr.hashCode()
        result = 31 * result + isNewEntry.hashCode()
        return result
    }


}