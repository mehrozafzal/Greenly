package greenely.greenely.feed.mappers

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.feed.models.ChartData
import greenely.greenely.feed.models.DataPoint
import greenely.greenely.feed.models.feeditems.MonthlyReport
import greenely.greenely.feed.models.json.DataPointJson
import greenely.greenely.feed.models.json.MonthlyReportJson
import greenely.greenely.feed.utils.FeedItemBodyFormatter
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

class MonthlyReportMapperTest {
    private lateinit var monthPointMapper: MonthlyPointMapper
    private lateinit var bodyFormatter: FeedItemBodyFormatter
    private lateinit var mapper: MonthlyReportMapper
    private val date = DateTime(2017, 1, 1, 0, 0)

}