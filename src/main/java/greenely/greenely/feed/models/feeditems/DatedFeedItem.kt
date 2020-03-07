package greenely.greenely.feed.models.feeditems

import org.joda.time.DateTime

interface DatedFeedItem {
    val date: DateTime
    fun read(): DatedFeedItem
}