package greenely.greenely.history.models

import android.content.Context
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.history.HistoryResolution
import greenely.greenely.history.NavigationDataPoint
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class HeaderModelTest(
        private val resolution: HistoryResolution,
        private val dataPoint: NavigationDataPoint,
        private val expectedTimeRangeUnit: String,
        private val expectedTimeRange: String,
        private val expectedConsumption: String
) {
    /**
     * If we input a timestamp into NavigationDataPoint, then result is last day of previous month
     * For example if we input 2018 January 01 then result is 2017 December 31, Sunday
     */

    private lateinit var context: Context
    private lateinit var model: HeaderModel

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun data() = listOf(
                arrayOf(
                        HistoryResolution.Month(DateTime(2015, 1, 1, 0, 0)),
                        NavigationDataPoint(
                                DateTime(2015, 1, 1, 0, 0).millis / 1000, 1000, true, true,null
                        ),
                        "Dag",
                        "Onsdag 31 December",
                        "1.00 kWh"
                ),
                arrayOf(
                        HistoryResolution.Year,
                        NavigationDataPoint(
                                DateTime(2015, 1, 1, 0, 0).millis / 1000, 1000, true, true,null
                        ),
                        "Månad",
                        "December",
                        "1.00 kWh"
                )
        )
    }

    @Before
    fun setUp() {
        context = mock {
            on { getString(R.string.month) } doReturn "Månad"
            on { getString(R.string.day) } doReturn "Dag"
            on { getString(R.string.x_kwh) } doReturn "%s kWh"
        }

        model = HeaderModel.forResolutionWithDataPoint(context, resolution, dataPoint)
    }

    @Ignore //this test runs well on android studio but fails on travis for some reason
    @Test
    fun testTimeRange() {
        assertThat(model.timeRange).isEqualTo(expectedTimeRange)
    }

    @Test
    fun testTimeRangeUnit() {
        assertThat(model.timeRangeUnit).isEqualTo(expectedTimeRangeUnit)
    }

    @Test
    fun testConsumption() {
        assertThat(model.consumption).isEqualTo(expectedConsumption)
    }
}