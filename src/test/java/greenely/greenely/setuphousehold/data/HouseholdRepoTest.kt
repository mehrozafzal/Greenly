package greenely.greenely.setuphousehold.data

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.squareup.moshi.Moshi
import greenely.greenely.api.GreenelyApi
import greenely.greenely.models.IntercomInfo
import greenely.greenely.models.IntercomProperties
import greenely.greenely.setuphousehold.mappers.HouseholdInputOptionsMapper
import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import greenely.greenely.setuphousehold.models.json.HouseholdConfigJsonModel
import greenely.greenely.setuphousehold.models.json.HouseholdRequestJsonModel
import greenely.greenely.store.UserStore
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

class HouseholdRepoTest {

    @JvmField
    @Rule
    val mockServer = MockWebServer()

    private lateinit var api: GreenelyApi
    private lateinit var userStore: UserStore
    private lateinit var repo: HouseholdRepo


    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        api = createApi()
        userStore = mock {
            on { token } doReturn "token"
        }

        repo = HouseholdRepo(
                api,
                userStore
        )
    }

    private fun createApi(): GreenelyApi {

        val moshi = Moshi.Builder()
                .add(HouseholdInputOptionsMapper())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(mockServer.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        return retrofit.create(GreenelyApi::class.java)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testSetHouseholdConfig() {

        //Given

        val expectedIntercomInfo = IntercomInfo(
                "1",
                "hash",
                "anton.holmberg@greenely.se"
        )
        val householdRequest = HouseholdRequestJsonModel(
                1,
                1,
                1,
                2,
                3,
                4,
                5,
                1,
                1,
                2
        )

        mockServer.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                            {
                                "data": {
                                    "account_setup_next_v3": "NO_NEXT_STEP",
                                    "properties": {
                                       "municipality": "stockholm",
                                         "facility_type": "villa",
                                         "heating_type": "heatingType",
                                         "secondary_heating_type": "",
                                         "tertiary_heating_type": "",
                                         "quaternary_heating_type": "",
                                         "facility_area": "20-90",
                                         "occupants": "4",
                                         "construction_year": "1900",
                                         "electric_car_count": "2"
                                    },
                                    "intercom": {
                                        "user_id": "1",
                                        "user_hash": "hash",
                                        "email": "anton.holmberg@greenely.se"
                                    }
                                },
                                 "jwt": "token"
                            }
                        """.trimIndent())
        )

        //when
        val response = repo.setHouseholdConfig(householdRequest).blockingFirst()

        // Then
        val request = mockServer.takeRequest()

        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine).isEqualTo("POST /v3/onboarding HTTP/1.1")
        assertThat(request.body.readString(Charset.forName("UTF-8"))).isEqualToIgnoringWhitespace("""
            {
                "construction_year_id":${householdRequest.constructionYearId},
                "electric_car_count_id":${householdRequest.electricCarCountId},
                "facility_area_id":${householdRequest.facilityAreaId},
                "facility_type_id":${householdRequest.facilityTypeId},
                "heating_type_id":${householdRequest.heatingTypeId},
                "municipality_id" :${householdRequest.municipalityId},
                "occupant_id":${householdRequest.occupants},
                "quaternary_heating_type_id":${householdRequest.quaternaryHeatingTypeId},
                "secondary_heating_type_id":${householdRequest.secondaryHeatingTypeId},
                "tertiary_heating_type_id":${householdRequest.tertiaryHeatingTypeId}
            }
        """.trimIndent())

        verify(userStore).token = "token"
        assertThat(response.intercom).isEqualTo(expectedIntercomInfo)
        assertThat(response.intercomProperties).isEqualTo(mapOf(
                Pair("municipality","stockholm"),
                Pair("facility_type","villa"),
                Pair("heating_type","heatingType"),
                Pair("secondary_heating_type",""),
                Pair("tertiary_heating_type",""),
                Pair("quaternary_heating_type",""),
                Pair("facility_area","20-90"),
                Pair("occupants","4"),
                Pair("construction_year","1900"),
                Pair("electric_car_count","2")
        ))

    }

    @Test
    fun testGetHouseholdConfig() {

        val expectedJson = HouseholdConfigJsonModel(
                "Intro Title",
                "Intro Text",
                listOf(HouseholdInputOptions(1, "Stockholm")),
                listOf(HouseholdInputOptions(1, "Villa")),
                listOf(HouseholdInputOptions(1, "Ved")),
                listOf(HouseholdInputOptions(1, "1993")),
                listOf(HouseholdInputOptions(1, "6")),
                listOf(HouseholdInputOptions(1, "1000")),
                listOf(HouseholdInputOptions(1, "4"))
        )

        //Given

        mockServer.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json")
                        .setBody("""
                            {
                                 "jwt": "token",
                                 "data": {
                                      "intro_title": "Intro Title",
                                      "intro_text": "Intro Text",
                                      "municipalities": [[1, "Stockholm"]],
                                      "facility_types": [[1, "Villa"]],
                                      "heating_types": [[1, "Ved"]],
                                      "facility_areas": [[1, "1000"]],
                                      "construction_years": [[1, "1993"]],
                                      "occupants": [[1, "6"]],
                                      "electric_car_counts": [[1, "4"]]
                                 }
                            }
                        """.trimIndent())
        )


        //when
        val response = repo.getHouseholdConfig().blockingFirst()

        //Then
        val request = mockServer.takeRequest()

        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine).isEqualTo("GET /v3/onboarding HTTP/1.1")

        verify(userStore).token = "token"
        assertThat(response).isEqualTo(expectedJson)

    }

    @Test
    fun testGetHouseholdConfigError() {
        mockServer.enqueue(
                MockResponse()
                        .setResponseCode(400)
        )

        var response: HouseholdConfigJsonModel? = null
        var error: Throwable? = null
        repo.getHouseholdConfig().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        assertThat(response).isNull()
        assertThat(error).isNotNull()

        assertThat(error).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun testSetHouseholdConfigError() {

        mockServer.enqueue(MockResponse().setResponseCode(400))

        var error: Throwable? = null
        HouseholdRequestJsonModel(
                1,
                1,
                1,
                2,
                3,
                4,
                6,
                4).let {

            repo.setHouseholdConfig(it).subscribeBy(
                    onError = {
                        error = it
                    }
            )
        }

        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
    }
}
