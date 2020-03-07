package greenely.greenely.retail.data

import com.squareup.moshi.Moshi
import greenely.greenely.api.GreenelyApi
import greenely.greenely.retail.models.*
import greenely.greenely.store.UserStore
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.junit.*
import org.mockito.Mockito
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class RetailRepoTest {

    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var userStore: UserStore
    private lateinit var repo: RetailRepo

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(
                        Moshi.Builder()
                                .build()
                ))
                .build()

        userStore = Mockito.mock(UserStore::class.java)
        repo = RetailRepo(retrofit.create(GreenelyApi::class.java), userStore)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testGetRetailOverviewResponse_isRetailCustomer() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                                    "is_retail_customer": true,
                                                    "can_become_retail_customer": true,
                                                    "greenely_fee_in_kr_per_month": 59,
                                                    "retail_state": "EMPTY",
                                                    "retail_start_date": null,
                                                    "retail_state_failed_message": null,
                                                    "current_month":{
                                                         "title": "Title",
                                                         "sub_title": "Subtitle",
                                                         "value": 12000,
                                                         "points":[
                                                            {
                                                               "cost_in_kr":100,
                                                               "timestamp":1551222000
                                                            },
                                                            {
                                                               "cost_in_kr":200,
                                                               "timestamp":1551304800
                                                            }
                                                         ]
                                                      },
                                                    "next_day":{
                                                         "title": "Title",
                                                         "sub_title": "Subtitle",
                                                         "value": 12000,
                                                         "points":[
                                                            {
                                                               "price":100,
                                                               "timestamp":1551222000
                                                            },
                                                            {
                                                               "price":200,
                                                               "timestamp":1551304800
                                                            }
                                                         ]
                                                      },
                                                    "current_day":{
                                                         "title": "Title",
                                                         "sub_title": "Subtitle",
                                                         "value": 12000,
                                                         "points":[
                                                            {
                                                               "price":100,
                                                               "timestamp":1551222000
                                                            },
                                                            {
                                                               "price":200,
                                                               "timestamp":1551304800
                                                            }
                                                         ]
                                                      },
                                                    "invoices": null
                                                },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        Mockito.`when`(userStore.token).thenReturn("token")

        var response: RetailOverViewJson? = null
        var error: Throwable? = null
        repo.getRetailOverviewResponse().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        Assertions.assertThat(request.requestLine).isEqualTo("GET /v2/retail/overview HTTP/1.1")
        Assertions.assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        Mockito.verify(userStore).token = "token"

        Assertions.assertThat(error).isNull()
        Assertions.assertThat(response).isNotNull()

        Assertions.assertThat(response).isEqualTo(
                RetailOverViewJson(
                        true,
                        true,
                        59,
                        false,
                        CurrentMonthDetailsJson("Title", "Subtitle", 12000, listOf(CurrentMonthPointsJson(100, "1551222000"), CurrentMonthPointsJson(200, "1551304800"))),
                        HeaderDetailsJson("Title", "Subtitle", 12000, listOf(PricingPointsJson(100, "1551222000"), PricingPointsJson(200, "1551304800"))),
                        HeaderDetailsJson("Title", "Subtitle", 12000, listOf(PricingPointsJson(100, "1551222000"), PricingPointsJson(200, "1551304800"))),
                        CustomerState.EMPTY,
                        null,
                        null,
                        mutableListOf(),
                        10f,null
                )
        )
    }


    @Test
    fun testGetRetailOverviewResponse_isNotRetailCustomer() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                                    "is_retail_customer": false,
                                                    "can_become_retail_customer": true,
                                                    "greenely_fee_in_kr_per_month": 59,
                                                    "retail_state": "EMPTY",
                                                    "retail_start_date": null,
                                                    "retail_state_failed_message": null,
                                                    "current_month" : null,
                                                    "next_day" : null,
                                                    "current_day" : null,
                                                    "invoices": null
                                                },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        Mockito.`when`(userStore.token).thenReturn("token")

        var response: RetailOverViewJson? = null
        var error: Throwable? = null
        repo.getRetailOverviewResponse().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        Assertions.assertThat(request.requestLine).isEqualTo("GET /v2/retail/overview HTTP/1.1")
        Assertions.assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

//        Mockito.verify(userStore).token = "token"

        Assertions.assertThat(error).isNull()
        Assertions.assertThat(response).isNotNull()

        Assertions.assertThat(response).isEqualTo(
                RetailOverViewJson(
                        false,
                        true,
                        59,
                        false,
                        null,
                        null,
                        null,
                        CustomerState.EMPTY,
                        null,
                        null,
                        mutableListOf(),
                        10f,null
                )

        )
    }



    @Test
    fun testGetRetailStateSuccessResponse() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                                    "is_retail_customer": false,
                                                    "can_become_retail_customer": true,
                                                    "retail_state":"OPEN"
                                                },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        Mockito.`when`(userStore.token).thenReturn("token")

        var response: RetailStateResponseModel? = null
        var error: Throwable? = null
        repo.getRetailState().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        Assertions.assertThat(request.requestLine).isEqualTo("GET /v1/retail/state HTTP/1.1")
        Assertions.assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        Assertions.assertThat(error).isNull()
        Assertions.assertThat(response).isNotNull()

        Assertions.assertThat(response).isEqualTo(
                RetailStateResponseModel("OPEN",false,true,150f,null)
        )
    }

    @Test
    fun testGetRetailStateErrorResponse() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        Mockito.`when`(userStore.token).thenReturn("token")

        var response: RetailStateResponseModel? = null
        var error: Throwable? = null
        repo.getRetailState().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        Assertions.assertThat(response).isNull()
        Assertions.assertThat(error).isNotNull()
        Assertions.assertThat(error).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun testGetRetailOverviewResponseError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        Mockito.`when`(userStore.token).thenReturn("token")

        var response: RetailOverViewJson? = null
        var error: Throwable? = null
        repo.getRetailOverviewResponse().subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        Assertions.assertThat(response).isNull()
        Assertions.assertThat(error).isNotNull()
        Assertions.assertThat(error).isInstanceOf(HttpException::class.java)
    }



}
