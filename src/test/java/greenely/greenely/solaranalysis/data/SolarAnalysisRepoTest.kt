package greenely.greenely.solaranalysis.data

import android.app.Application
import androidx.databinding.ObservableInt
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import greenely.greenely.api.GreenelyApi
import greenely.greenely.solaranalysis.mappers.AnalysisMapper
import greenely.greenely.solaranalysis.mappers.ContactMeRequestMapper
import greenely.greenely.solaranalysis.mappers.HouseholdInfoMapper
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.AnalysisJson
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.HouseholdInfo
import greenely.greenely.store.UserStore
import greenely.greenely.utils.CurrencyFormatUtils
import greenely.greenely.utils.NonNullObservableField
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.nio.charset.Charset

class SolarAnalysisRepoTest {
    @JvmField
    @Rule
    val mockServer = MockWebServer()

    private lateinit var api: GreenelyApi
    private lateinit var userStore: UserStore
    private lateinit var repo: SolarAnalysisRepo
    private lateinit var householdMapper: HouseholdInfoMapper
    private lateinit var analysisMapper: AnalysisMapper
    private lateinit var contactMeRequestMapper: ContactMeRequestMapper

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val application: Application = mock {
            on { getString(R.string.x_kwh) } doReturn "%s kWh"
            on { getString(R.string.x_currency) } doReturn "%s kr"
            on { getString(R.string.x_years) } doReturn "%s år"
        }

        api = createApi()
        userStore = mock {
            on { token } doReturn "token"
        }

        val currencyFormatter = CurrencyFormatUtils(application)

        householdMapper = HouseholdInfoMapper()
        analysisMapper = AnalysisMapper(application, currencyFormatter)
        contactMeRequestMapper = ContactMeRequestMapper()

        repo = SolarAnalysisRepo(
                api,
                userStore,
                analysisMapper,
                householdMapper,
                contactMeRequestMapper
        )
    }

    private fun createApi(): GreenelyApi {
        val retrofit = Retrofit.Builder()
                .baseUrl(mockServer.url("/"))
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(GreenelyApi::class.java)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testSendHousehold() {
        // Given
        val household = HouseholdInfo(
                address = NonNullObservableField("Address"),
                postalCode = NonNullObservableField("Postal code"),
                postalRegion = NonNullObservableField("Postal region"),
                roofSizeId = ObservableInt(R.id.medium),
                roofAngleId = ObservableInt(R.id.mediumAngle),
                roofDirectionId = ObservableInt(R.id.south)
        )
        val householdJson = householdMapper.toJsonData(household)

        mockServer.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                            {
                            "jwt": "token",
                            "data": {
                                "id": 12,
                                "version": 2,
                                "created": "%Y-%m-%d %H:%M",
                                "payload": {
                                    "direction": 0,
                                    "roof_angle": 10,
                                    "area": 30.0,
                                    "address": "Address Postal code Postal region",
                                    "estimated_cost_after_solar_support": 12,
                                    "yearly_saving": 12,
                                    "yearly_production": 12,
                                    "payback_time_with_solar_support": 12,
                                    "month_data": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
                                    "potential_saving": 12,
                                    "total_saving": 12,
                                    "solar_panel_lifespan": 12
                                    }
                                }
                            }
                        """.trimIndent())
        )

        // When
        val analysis = repo.sendHouseholdInfo(household).blockingFirst()

        // Then
        val request = mockServer.takeRequest()

        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine).isEqualTo("POST /v2/guide/solar/estimate HTTP/1.1")
        assertThat(request.body.readString(Charset.forName("UTF-8"))).isEqualToIgnoringWhitespace("""
            {
                "address": "${householdJson.address}",
                "area": ${householdJson.roofArea},
                "direction": ${householdJson.direction},
                "roof_angle": ${householdJson.roofAngle}
            }
        """.trimIndent())

        val expectedAnalysisJson = AnalysisJson(
                12,
                12,
                12,
                12f,
                12,
                12,
                12,
                listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f)
        )
        verify(userStore).token = "token"
        assertThat(analysis).isEqualTo(analysisMapper.fromAnalysisJson(expectedAnalysisJson,"12"))
    }

    @Test
    fun testValidateAddress() {
        // Given
        val household = HouseholdInfo(
                address = NonNullObservableField("Address"),
                postalCode = NonNullObservableField("Postal code"),
                postalRegion = NonNullObservableField("Postal region"),
                roofSizeId = ObservableInt(R.id.medium),
                roofAngleId = ObservableInt(R.id.mediumAngle),
                roofDirectionId = ObservableInt(R.id.south)
        )
        val addressValidationRequest = householdMapper.toAddressValidationRequest(household)

        mockServer.enqueue(
                MockResponse()
                        .setBody("""
                            {
                                "jwt": "token"
                            }
                        """.trimIndent())
                        .setResponseCode(200)
        )

        // When
        repo.validateAddress(household).subscribe()

        // Then
        val request = mockServer.takeRequest()

        verify(userStore).token = "token"
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine).isEqualTo("POST /v2/guide/solar/address HTTP/1.1")
        assertThat(request.body.readString(Charset.forName("UTF-8"))).isEqualToIgnoringWhitespace(
                """
                    {
                        "address": "${addressValidationRequest.address}"
                    }
                """.trimIndent()
        )
    }

    @Test
    fun testValidateAddressError() {
        // Given
        val household = HouseholdInfo(
                address = NonNullObservableField("Address"),
                postalCode = NonNullObservableField("Postal code"),
                postalRegion = NonNullObservableField("Postal region"),
                roofSizeId = ObservableInt(R.id.medium),
                roofAngleId = ObservableInt(R.id.mediumAngle),
                roofDirectionId = ObservableInt(R.id.south)
        )
        mockServer.enqueue(MockResponse().setResponseCode(400))

        // When
        var error: Throwable? = null
        repo.validateAddress(household).subscribeBy(onError = { error = it })

        // Then
        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun sendContactInformation() {
        // Given
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

        val expectedJson = contactMeRequestMapper.from(analysis, contactInfo)

        mockServer.enqueue(
                MockResponse()
                        .setBody("""
                            {
                                "jwt": "token"
                            }
                        """.trimIndent())
                        .setResponseCode(200)
        )

        // When
        repo.sendContactInformation(analysis, contactInfo).subscribe()

        // Then
        val request = mockServer.takeRequest()

        verify(userStore).token = "token"
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.requestLine).isEqualTo("POST /v2/guide/solar/contact HTTP/1.1")
        assertThat(request.body.readString(Charset.forName("UTF-8"))).isEqualToIgnoringWhitespace("""
               {
                   "email": "${expectedJson.email}",
                   "id": "${expectedJson.id}",
                   "name": "${expectedJson.name}",
                   "phone_number": "${expectedJson.phoneNumber}"
                   }
        """.trimIndent())
    }
}