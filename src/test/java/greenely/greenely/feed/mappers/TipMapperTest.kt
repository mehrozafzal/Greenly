package greenely.greenely.feed.mappers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.feed.models.feeditems.Tip
import greenely.greenely.feed.models.json.TipJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class TipMapperTest {
    private lateinit var bodyFormatter: FeedItemBodyFormatter
    private lateinit var mapper: TipMapper

    @Before
    fun setUp() {
        bodyFormatter = mock {
            on { formatBody(any()) } doReturn "Body"
        }

        mapper = TipMapper(bodyFormatter)
    }

    @Test
    fun testFromJson() {
        // Given
        val date = DateTime(2017, 1, 1, 0, 0)
        val json = TipJson(
                date.millis / 1000,
                "Title",
                "Some formatted body",
                true
        )

        // When
        val tip = mapper.fromJson(json)

        // Then
        assertThat(tip).isEqualTo(
                Tip(
                        date,
                        "Title",
                        "Body",
                        true
                )
        )
    }
}