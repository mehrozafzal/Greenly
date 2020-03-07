package greenely.greenely.guidance.mappers

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.guidance.models.LatestSolarAnalysis
import greenely.greenely.guidance.models.json.LatestSolarAnalysisJson
import greenely.greenely.guidance.models.json.LatestSolarAnalysisResponseJson
import greenely.greenely.utils.CurrencyFormatUtils
import org.assertj.core.api.Assertions
import org.junit.Test

class LatestSolarAnalysisMapperTest{

@Test
fun testFromLatestSolarAnalysisJson() {
    //Given

    val application: Application = mock {
        on { getString(R.string.x_kwh) } doReturn "%s kWh"
        on { getString(R.string.x_currency) } doReturn "%s kr"
        on { getString(R.string.x_years) } doReturn "%s år"
        on { getString(R.string.currency_symbol) } doReturn "kr"
    }

    val currencyFormatter = CurrencyFormatUtils(application)

    val mapper = LatestSolarAnalysisMapper(application, currencyFormatter)
    val json = LatestSolarAnalysisJson(
            totalSaving = 12,
            estimatedCostAfterSolarSupport = 12,
            yearlySaving = 12,
            yearlyProduction = 12f,
            potentialSaving = 12,
            paybackTimeWithSolarSupport = 12,
            solarPanelLifespan = 12,
            monthData = mutableListOf()
    )
    val responseJson = LatestSolarAnalysisResponseJson(
            id = "12",
            version = 12,
            date = "2018-01-01",
            payload = json
    )

    //when
    val result = mapper.fromLatestSolarAnalysisJson(json, responseJson.date)

    //then
    Assertions.assertThat(result).isEqualTo(
            LatestSolarAnalysis(
                    totalSaving = "12 kr",
                    estimatedCostAfterSolarSupport = "12 kr",
                    yearlySaving = "12 kr",
                    yearlyProduction = "12.0 kWh",
                    potentialSaving = "12 kr",
                    paybackTimeWithSolarSupport = "12 år",
                    solarPanelLifeSpan = "12 år",
                    createdDate = "2018-01-01",
                    _monthData = mutableListOf()
            )
    )
}
}

