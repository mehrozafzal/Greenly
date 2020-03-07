package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.Tip
import greenely.greenely.feed.models.json.TipJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.feed.utils.TimeAgoStrategy
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class TipMapper @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(tipJson: TipJson): Tip {
        val date = DateTime(tipJson.timestamp * 1000L)
        return Tip(
                date,
                tipJson.title,
                feedItemBodyFormatter.formatBody(tipJson.body),
                tipJson.isNewEntry
        )
    }
}

