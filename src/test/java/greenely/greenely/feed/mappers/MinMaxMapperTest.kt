package greenely.greenely.feed.mappers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.feed.models.feeditems.MinMax
import greenely.greenely.feed.models.json.MinMaxJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class MinMaxMapperTest {
    private lateinit var bodyFormatter: FeedItemBodyFormatter
    private lateinit var mapper: MinMaxMapper

    @Before
    fun setUp() {
        bodyFormatter = mock {
            on { formatBody(any()) } doReturn "Body"
        }

        mapper = MinMaxMapper(bodyFormatter)
    }

    @Test
    fun testFromJson() {
        // Given
        val date = DateTime(2017, 1, 1, 0, 0)
        val json = MinMaxJson(
                date.millis / 1000,
                "Title",
                "Some formatted body",
                true
        )

        // When
        val minMax = mapper.fromJson(json)

        // Then
        assertThat(minMax).isEqualTo(
                MinMax(
                        date,
                        "Title",
                        "Body",
                        true
                )
        )
    }
}