package greenely.greenely.feed.models.feeditems

import org.joda.time.DateTime

data class Tip(
        override val date: DateTime,
        val title: String,
        val body: CharSequence,
        var isNewEntry: Boolean
) : DatedFeedItem {
    override fun read() = this.copy(isNewEntry = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tip

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

