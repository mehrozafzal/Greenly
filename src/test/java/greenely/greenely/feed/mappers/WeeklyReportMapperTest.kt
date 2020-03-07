package greenely.greenely.feed.mappers

import android.text.style.BulletSpan
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.DataPoint
import greenely.greenely.feed.models.feeditems.WeeklyReport
import greenely.greenely.feed.models.json.DataPointJson
import greenely.greenely.feed.models.json.WeeklyReportJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class WeeklyReportMapperTest {
    private lateinit var bodyFormatter: FeedItemBodyFormatter
    private lateinit var dataPointMapper: DataPointMapper
    private lateinit var mapper: WeeklyReportMapper

    private val date = DateTime(2017, 1, 1, 0, 0)

    @Before
    fun setUp() {
        bodyFormatter = mock {
            on { formatBody(any()) } doReturn "Body"
        }
        dataPointMapper = mock {
            on { fromJson(any()) } doReturn DataPoint(date, 200)
        }

        mapper = WeeklyReportMapper(bodyFormatter, dataPointMapper)
    }

    @Test
    fun testFromJson() {
        // Given
        val json = WeeklyReportJson(
                null,
                date.millis / 1000,
                "Title",
                "Some formatted body",
                true,
                0f,
                0f,
                listOf(DataPointJson(date.millis / 1000, 200,200,200))
        )

        // When
        val weeklyReport = mapper.fromJson(json)

        // Then
        assertThat(weeklyReport).isEqualTo(
                WeeklyReportJson(
                        null,
                        date.millis / 1000,
                        "Title",
                        "Some formatted body",
                        true,
                        0f,
                        0f,
                        listOf(DataPointJson(date.millis / 1000, 200,200,200))
                )
        )
    }
}