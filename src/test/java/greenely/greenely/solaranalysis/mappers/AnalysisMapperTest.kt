package greenely.greenely.solaranalysis.mappers

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.AnalysisJson
import greenely.greenely.utils.CurrencyFormatUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class AnalysisMapperTest {

    private lateinit var mapper: AnalysisMapper

    @Before
    fun setUp() {
        val application: Application = mock {
            on { getString(R.string.x_kwh) } doReturn "%s kWh"
            on { getString(R.string.x_currency) } doReturn "%s kr"
            on { getString(R.string.x_years) } doReturn "%s år"
            on { getString(R.string.currency_symbol) } doReturn "kr"
        }

        val currencyFormatter = CurrencyFormatUtils(application)

        mapper = AnalysisMapper(application, currencyFormatter)
    }

    @Test
    fun fromAnalysisJson() {
        // Given
        val json = AnalysisJson(
                1000,
                1000,
                10,
                1234.0f,
                3,
                3,
                12,
                listOf(1f, 2f, 3f, 4f, 5f, 6f)
        )

        // When
        val analysis = mapper.fromAnalysisJson(json, "12")

        // Then
        val expected = Analysis(
                "12",
                "1,000 kr",
                "1,000 kr",
                "10 kr",
                "1234 kWh",
                "3 kr",
                "3 år",
                "12 år",
                mutableListOf(1f, 2f, 3f, 4f, 5f, 6f)
        )
        assertThat(analysis).isEqualTo(expected)
    }

    @Test
    fun testToAnalysisJson() {
        val analysis = Analysis(
                "12",
                "1000 kr",
                "1000 kr",
                "10 kr",
                "1234 kWh",
                "3 kr",
                "3 år",
                "12 år",
                mutableListOf(1f, 2f, 3f, 4f, 5f, 6f)
        )

        // When
        val json = mapper.toAnalysisJson(analysis)

        val expected = AnalysisJson(
                1000,
                1000,
                10,
                1234.0f,
                3,
                3,
                12,
                listOf(1f, 2f, 3f, 4f, 5f, 6f)
        )
        assertThat(json).isEqualTo(expected)
    }

}