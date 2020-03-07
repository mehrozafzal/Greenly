package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.News
import greenely.greenely.feed.models.json.NewsJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.feed.utils.TimeAgoStrategy
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class NewsMapper @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(newsJson: NewsJson): News {
        val date = DateTime(newsJson.timestamp * 1000L)
        return News(
                date,
                newsJson.title,
                feedItemBodyFormatter.formatBody(newsJson.body),
                newsJson.isNewEntry
        )
    }
}

