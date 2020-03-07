@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.history

import greenely.greenely.api.GreenelyApi
import greenely.greenely.store.UserStore
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class HistoryRepoTest {
    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var repoFactory: HistoryRepoFactory

    private lateinit var userStore: UserStore

    @Before
    fun setUp() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        userStore = mock(UserStore::class.java)
        repoFactory = HistoryRepoFactory(
                retrofit.create(GreenelyApi::class.java),
                userStore
        )
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun testGetHistory_year() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "state": 3,
                                            "years": [
                                                {
                                                    "year": 2017,
                                                    "months": [
                                                        {
                                                            "timestamp": 1489907200,
                                                            "usage": null,
                                                            "selectable": true,
                                                            "hasDetailedData": false
                                                        }
                                                    ]
                                                }
                                            ],
                                            "maxValue": 957800,
                                            "components": [
                                                "usage",
                                                "temperature",
                                                "distribution",
                                                "min_max"
                                            ]
                                        },
                                        "jwt": "I am a jwt"
                                    }
                                """.trimIndent()
                        )
        )

        val repo = repoFactory.create(HistoryResolution.Year)

        `when`(userStore.token).thenReturn("token")

        var historyResponse: HistoryResponse? = null
        var error: Throwable? = null
        repo.getHistory().subscribeBy(
                onNext = {
                    historyResponse = it
                },
                onError = {
                    error = it
                }
        )

        val recordedRequest = server.takeRequest()

        assertThat(recordedRequest.requestLine).isEqualToIgnoringCase("GET /v3/data HTTP/1.1")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "I am a jwt"

        assertThat(error).isNull()
        assertThat(historyResponse).isNotNull()
        assertThat(historyResponse?.components).isEqualTo(listOf(
                HistoryComponent.USAGE,
                HistoryComponent.TEMPERATURE,
                HistoryComponent.DISTRIBUTION,
                HistoryComponent.MIN_MAX
        ))
        assertThat(historyResponse?.state).isEqualTo(HistoryState.HAS_DATA)
        assertThat(historyResponse?.navigationData).isEqualTo(listOf(
                NavigationData(
                        2017,
                        957800,
                        listOf(
                                NavigationDataPoint(
                                        1489907200,
                                        null,
                                        true,
                                        false,
                                        null
                                )
                        )
                )
        ))
    }

    @Test
    fun testGetHistory_week() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "weeks": [
                                                {
                                                    "weekNumber": 2,
                                                    "days": [
                                                        {
                                                            "timestamp": 1489907200,
                                                            "usage": 141300,
                                                            "selectable": true,
                                                            "hasDetailedData": true
                                                        }
                                                    ]
                                                }
                                            ],
                                            "state": 3,
                                            "maxValue": 957800,
                                            "components": [
                                                "usage",
                                                "temperature",
                                                "distribution",
                                                "min_max"
                                            ]
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")
        val date = DateTime.now()
        val repo = repoFactory.create(HistoryResolution.Month(date))

        var error: Throwable? = null
        var historyResponse: HistoryResponse? = null
        repo.getHistory().subscribeBy(
                onNext = {
                    historyResponse = it
                },
                onError = {
                    error = it
                }
        )

        val recordedRequest = server.takeRequest()

        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(recordedRequest.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/weeks HTTP/1.1")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(historyResponse).isNotNull()
        assertThat(historyResponse?.components).isEqualTo(listOf(
                HistoryComponent.USAGE,
                HistoryComponent.TEMPERATURE,
                HistoryComponent.DISTRIBUTION,
                HistoryComponent.MIN_MAX
        ))
        assertThat(historyResponse?.state).isEqualTo(HistoryState.HAS_DATA)
        assertThat(historyResponse?.navigationData).isEqualTo(listOf(
                NavigationData(
                        2,
                        957800,
                        listOf(
                                NavigationDataPoint(
                                        1489907200,
                                        141300,
                                        true,
                                        true,
                                        null
                                )
                        )
                )
        ))
    }

    @Test
    fun testGetHistory_yearError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        val repo = repoFactory.create(HistoryResolution.Year)

        `when`(userStore.token).thenReturn("token")

        var historyResponse: HistoryResponse? = null
        var error: Throwable? = null
        repo.getHistory().subscribeBy(
                onNext = {
                    historyResponse = it
                },
                onError = {
                    error = it
                }
        )

        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
        assertThat(historyResponse).isNull()
    }

    @Test
    fun testGetHistory_weekError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        val repo = repoFactory.create(HistoryResolution.Month(DateTime.now()))

        `when`(userStore.token).thenReturn("token")

        var historyResponse: HistoryResponse? = null
        var error: Throwable? = null
        repo.getHistory().subscribeBy(
                onNext = {
                    historyResponse = it
                },
                onError = {
                    error = it
                }
        )

        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
        assertThat(historyResponse).isNull()
    }

    @Test
    fun testGetUsage_day() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "points": [
                                                {
                                                    "timestamp": 1483228800,
                                                    "usage": 250
                                                }
                                            ],
                                            "maxValue": 270
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Month(DateTime.now()))

        val date = DateTime.now()

        var usageResponse: UsageResponse? = null
        var error: Throwable? = null
        repo.getUsage(date).subscribeBy(
                onNext = {
                    usageResponse = it
                },
                onError = {
                    error = it
                }
        )

        val recordedRequest = server.takeRequest()

        assertThat(recordedRequest.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/${date.dayOfMonth}/usage HTTP/1.1")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(usageResponse).isNotNull()
        assertThat(usageResponse?.max).isEqualTo(0.270f)
        assertThat(usageResponse?.points).isEqualTo(listOf(
                Consumption(
                        1483228800,
                        250
                )
        ))
    }

    @Test
    fun testGetUsage_month() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "points": [
                                                {
                                                    "timestamp": 1483228800,
                                                    "usage": 25730
                                                }
                                            ],
                                            "maxValue": 28000
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)

        val date = DateTime.now()

        var usageResponse: UsageResponse? = null
        var error: Throwable? = null
        repo.getUsage(date).subscribeBy(
                onNext = {
                    usageResponse = it
                },
                onError = {
                    error = it
                }
        )

        val recordedRequest = server.takeRequest()

        assertThat(recordedRequest.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/usage HTTP/1.1")
        assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("JWT token")
        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(usageResponse).isNotNull()
        assertThat(usageResponse?.max).isEqualTo(28f)
        assertThat(usageResponse?.points).isEqualTo(listOf(
                Consumption(
                        1483228800,
                        25730
                )
        ))
    }

    @Test
    fun testGetUsage_dayError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Month(DateTime.now()))

        val date = DateTime.now()

        var usageResponse: UsageResponse? = null
        var error: Throwable? = null
        repo.getUsage(date).subscribeBy(
                onNext = {
                    usageResponse = it
                },
                onError = {
                    error = it
                }
        )

        assertThat(usageResponse).isNull()
        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun testGetUsage_monthError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)

        val date = DateTime.now()

        var usageResponse: UsageResponse? = null
        var error: Throwable? = null
        repo.getUsage(date).subscribeBy(
                onNext = {
                    usageResponse = it
                },
                onError = {
                    error = it
                }
        )

        assertThat(usageResponse).isNull()
        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
    }


    @Test
    fun testGetTemperature_month() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                  {
                                      "data": {
                                          "points": [
                                              {
                                                  "timestamp": 1483228800,
                                                  "temperature": -240,
                                                  "usage": 25730
                                              }
                                          ],
                                          "maxUsageValue": 28000,
                                          "minTemperatureValue": -270,
                                          "maxTemperatureValue": -110
                                      },
                                      "jwt": "token"
                                  }
                                """.trimIndent()
                        )
        )
        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)

        val date = DateTime.now()

        var error: Throwable? = null
        var response: TemperatureResponse? = null
        repo.getTemperature(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/temperature HTTP/1.1")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response?.temperatureMax).isEqualTo(-1.1f)
        assertThat(response?.temperatureMin).isEqualTo(-2.7f)
        assertThat(response?.usageMax).isEqualTo(28f)
        assertThat(response?.points).isEqualTo(listOf(
                Temperature(
                        1483228800,
                        25730,
                        -240
                )
        ))
    }

    @Test
    fun testTemperature_day() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "points": [
                                                {
                                                    "timestamp": 1483228800,
                                                    "temperature": -240,
                                                    "usage": 250
                                                }
                                            ],
                                            "maxUsageValue": 280,
                                            "minTemperatureValue": -270,
                                            "maxTemperatureValue": -110
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()

        val repo = repoFactory.create(HistoryResolution.Month(date))

        var error: Throwable? = null
        var response: TemperatureResponse? = null
        repo.getTemperature(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/${date.dayOfMonth}/temperature HTTP/1.1")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response?.temperatureMax).isEqualTo(-1.1f)
        assertThat(response?.temperatureMin).isEqualTo(-2.7f)
        assertThat(response?.usageMax).isEqualTo(0.28f)
        assertThat(response?.points).isEqualTo(listOf(
                Temperature(
                        1483228800,
                        250,
                        -240
                )
        ))
    }

    @Test
    fun testGetTemperature_monthError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )
        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)

        val date = DateTime.now()

        var error: Throwable? = null
        var response: TemperatureResponse? = null
        repo.getTemperature(date).subscribeBy(
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
    fun testGetTemperature_dayError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()

        val repo = repoFactory.create(HistoryResolution.Month(date))

        var error: Throwable? = null
        var response: TemperatureResponse? = null
        repo.getTemperature(date).subscribeBy(
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
    fun testDistribution_month() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                   {
                                        "data": [
                                            {
                                                "start": "06:00",
                                                "end": "14:00",
                                                "percentage": 6000
                                            },
                                            {
                                                "start": "15:00",
                                                "end": "23:00",
                                                "percentage": 3000
                                            },
                                            {
                                                "start": "24:00",
                                                "end": "05:00",
                                                "percentage": 1000
                                            }
                                        ],
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        val date = DateTime.now()

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)


        var error: Throwable? = null
        var response: List<DistributionDataPoint>? = null
        repo.getDistribution(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/distribution HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isEqualTo(listOf(
                DistributionDataPoint(
                        "06:00",
                        "14:00",
                        6000
                ),
                DistributionDataPoint(
                        "15:00",
                        "23:00",
                        3000
                ),
                DistributionDataPoint(
                        "24:00",
                        "05:00",
                        1000
                )
        ))
    }

    @Test
    fun getDistribution_day() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                   {
                                        "data": [
                                            {
                                                "start": "06:00",
                                                "end": "14:00",
                                                "percentage": 6000
                                            },
                                            {
                                                "start": "15:00",
                                                "end": "23:00",
                                                "percentage": 3000
                                            },
                                            {
                                                "start": "24:00",
                                                "end": "05:00",
                                                "percentage": 1000
                                            }
                                        ],
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        val date = DateTime.now()

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Month(date))


        var error: Throwable? = null
        var response: List<DistributionDataPoint>? = null
        repo.getDistribution(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/${date.dayOfMonth}/distribution HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isEqualTo(listOf(
                DistributionDataPoint(
                        "06:00",
                        "14:00",
                        6000
                ),
                DistributionDataPoint(
                        "15:00",
                        "23:00",
                        3000
                ),
                DistributionDataPoint(
                        "24:00",
                        "05:00",
                        1000
                )
        ))
    }

    @Test
    fun testDistribution_monthError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        val date = DateTime.now()

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Year)


        var error: Throwable? = null
        var response: List<DistributionDataPoint>? = null
        repo.getDistribution(date).subscribeBy(
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
    fun testDistribution_dayError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        val date = DateTime.now()

        `when`(userStore.token).thenReturn("token")
        val repo = repoFactory.create(HistoryResolution.Month(date))


        var error: Throwable? = null
        var response: List<DistributionDataPoint>? = null
        repo.getDistribution(date).subscribeBy(
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
    fun testMinMax_month() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                   {
                                        "data": {
                                            "points": [
                                                {
                                                    "timestamp": 1483228800,
                                                    "minimum": null,
                                                    "maximum": 25730,
                                                    "usage": 25730
                                                }
                                            ],
                                            "maxValue": 28000
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()
        val repo = repoFactory.create(HistoryResolution.Year)

        var error: Throwable? = null
        var response: MinMaxResponse? = null
        repo.getMinMax(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/min_max HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response?.max).isEqualTo(28f)
        assertThat(response?.points).isEqualTo(listOf(
                MinMaxDataPoint(
                        1483228800,
                        null,
                        25730,
                        25730
                )
        ))
    }

    @Test
    fun testMinMax_day() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                   {
                                        "data": {
                                            "points": [
                                                {
                                                    "timestamp": 1483228800,
                                                    "minimum": null,
                                                    "maximum": 25730,
                                                    "usage": 25730
                                                }
                                            ],
                                            "maxValue": 28000
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()
        val repo = repoFactory.create(HistoryResolution.Month(date))

        var error: Throwable? = null
        var response: MinMaxResponse? = null
        repo.getMinMax(date).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("GET /v3/data/${date.year}/${date.monthOfYear}/${date.dayOfMonth}/min_max HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response?.max).isEqualTo(28f)
        assertThat(response?.points).isEqualTo(listOf(
                MinMaxDataPoint(
                        1483228800,
                        null,
                        25730,
                        25730
                )
        ))
    }

    @Test
    fun testMinMax_monthError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()
        val repo = repoFactory.create(HistoryResolution.Year)

        var error: Throwable? = null
        var response: MinMaxResponse? = null
        repo.getMinMax(date).subscribeBy(
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
    fun testMinMax_dayError() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")

        val date = DateTime.now()
        val repo = repoFactory.create(HistoryResolution.Month(date))

        var error: Throwable? = null
        var response: MinMaxResponse? = null
        repo.getMinMax(date).subscribeBy(
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
    fun testFactory_cacheYear() {
        assertThat(repoFactory.create(HistoryResolution.Year))
                .isEqualTo(repoFactory.create(HistoryResolution.Year))
    }

    @Test
    fun testFactory_cacheWeek() {
        val firstDate = DateTime.now()
        assertThat(repoFactory.create(HistoryResolution.Month(firstDate)))
                .isEqualTo(repoFactory.create(HistoryResolution.Month(firstDate)))

        val secondDate = DateTime.now().minusDays(1)
        assertThat(repoFactory.create(HistoryResolution.Month(secondDate)))
                .isNotEqualTo(repoFactory.create(HistoryResolution.Month(firstDate)))
    }
}

