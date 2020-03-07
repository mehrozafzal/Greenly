package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.MinMax
import greenely.greenely.feed.models.json.MinMaxJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.feed.utils.TimeAgoStrategy
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class MinMaxMapper @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(minMaxJson: MinMaxJson): MinMax {
        val date = DateTime(minMaxJson.timestamp * 1000L)
        return MinMax(
                date,
                minMaxJson.title,
                feedItemBodyFormatter.formatBody(minMaxJson.body),
                minMaxJson.isNewEntry
        )
    }
}

