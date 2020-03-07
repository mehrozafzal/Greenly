package greenely.greenely.solaranalysis.mappers

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.ContactMeRequest
import greenely.greenely.utils.CurrencyFormatUtils
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


class ContactMeRequestMapperTest {
    private lateinit var householdMapper: HouseholdInfoMapper
    private lateinit var analysisMapper: AnalysisMapper
    private lateinit var application: Application
    private lateinit var mapper: ContactMeRequestMapper

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.x_kwh) } doReturn "%s kWh"
            on { getString(R.string.x_currency) } doReturn "%s kr"
            on { getString(R.string.x_years) } doReturn "%s år"
        }

        val currencyFormatter = CurrencyFormatUtils(application)

        householdMapper = HouseholdInfoMapper()
        analysisMapper = AnalysisMapper(application, currencyFormatter)

        mapper = ContactMeRequestMapper()
    }

    @Test
    fun testFrom() {
        val analysis = Analysis(
                id = "12",
                totalSaving = "12",
                estimatedCostAfterSolarSupport = "12",
                yearlySaving = "12",
                yearlyProduction = "12",
                potentialSaving = "12",
                paybackTimeWithSolarSupport = "12",
                solarPanelLifeSpan = "12",
                _monthData = mutableListOf()
        )
        val contactInfo = ContactInfo(
                name = NonNullObservableField("Anton"),
                email = NonNullObservableField("anton@test.com"),
                phoneNumber = NonNullObservableField("070 000 00 00")
        )
        val contactMeRequest = ContactMeRequest(
                name = "Anton",
                email = "anton@test.com",
                phoneNumber = "070 000 00 00",
                id = "12"
        )

        // When
        val result = mapper.from(analysis, contactInfo)

        // Then
        assertThat(result).isEqualTo(contactMeRequest)
    }
}