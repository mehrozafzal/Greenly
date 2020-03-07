package greenely.greenely.feed.mappers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.feed.models.feeditems.Message
import greenely.greenely.feed.models.json.MessageJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class PushMessageMapperTest {
    private lateinit var bodyFormatter: FeedItemBodyFormatter
    private lateinit var mapper: MessageMapper

    @Before
    fun setUp() {
        bodyFormatter = mock {
            on { formatBody(any()) } doReturn "Body"
        }

        mapper = MessageMapper(bodyFormatter)
    }

    @Test
    fun testFromJson() {
        // Given
        val date = DateTime(2017, 1, 1, 0, 0)
        val json = MessageJson(
                date.millis / 1000,
                "Title",
                "Some formatterd body",
                true
        )

        // When
        val message = mapper.fromJson(json)

        // Then
        assertThat(message).isEqualTo(
                Message(
                        date,
                        "Title",
                        "Body",
                        true
                )
        )
    }
}