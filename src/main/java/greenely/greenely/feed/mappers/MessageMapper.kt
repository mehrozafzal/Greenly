package greenely.greenely.feed.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.feed.models.feeditems.Message
import greenely.greenely.feed.models.json.MessageJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import greenely.greenely.feed.utils.TimeAgoStrategy
import org.joda.time.DateTime
import javax.inject.Inject

@OpenClassOnDebug
class MessageMapper @Inject constructor(
        private val feedItemBodyFormatter: FeedItemBodyFormatter
) {
    fun fromJson(messageJson: MessageJson): Message {
        val date = DateTime(messageJson.timestamp * 1000L)
        return Message(
                date,
                messageJson.title,
                feedItemBodyFormatter.formatBody(messageJson.body),
                messageJson.isNewEntry
        )
    }
}

