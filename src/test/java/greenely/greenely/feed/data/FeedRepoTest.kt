@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.feed.data

import com.nhaarman.mockito_kotlin.*
import greenely.greenely.api.GreenelyApi
import greenely.greenely.feed.mappers.*
import greenely.greenely.feed.models.feeditems.*
import greenely.greenely.feed.models.json.MessageJson
import greenely.greenely.feed.models.json.PredictedInvoiceJson
import greenely.greenely.feed.models.json.PredictedYearlySavingsJson
import greenely.greenely.store.UserStore
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class FeedRepoTest {
    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var userStore: UserStore

    private val message = Message(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true
    )
    private val minMax = MinMax(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true
    )
    private val monthlyReport = MonthlyReport(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true, null
    )
    private val news = News(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true
    )
    private val tip = Tip(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true
    )
    private val weeklyReport = WeeklyReport(
            DateTime(2017, 1, 1, 0, 0), "Title", "Body", true, null,null,"","-",""
    )

    private val costAnalysisFeedItem=CostAnalysisFeedItem(DateTime(2017, 1, 1, 0, 0),"","","",0f,0f,0f,0f,""
    ,"","",0f,0f,"","",true,null)

    private val predictedInvoiceItem=PredictedInvoiceItem(DateTime(2017, 1, 1, 0, 0),"","","","","",false)

    private val predictedYearlySavingsFeedItem=PredictedYearlySavingsFeedItem(DateTime(2017, 1, 1, 0, 0),"","","","",false)

    private val predictedInvoiceJson=PredictedInvoiceJson("",1,PredictedInvoiceJson.Extra(0f,""),false,"","")
    private val predictedYearlySavingJson=PredictedYearlySavingsJson("",1,PredictedYearlySavingsJson.Extra(0f),false,"","")


    private lateinit var minMaxMapper: MinMaxMapper
    private lateinit var monthlyReportMapper: MonthlyReportMapper
    private lateinit var messageMapper: MessageMapper
    private lateinit var newsMapper: NewsMapper
    private lateinit var tipMapper: TipMapper
    private lateinit var weeklyReportMapper: WeeklyReportMapper
    private lateinit var costAnalysisMapper: CostAnalysisMapper
    private lateinit var predictedSavingsMapper: PredictedSavingsMapper


    private lateinit var repo: FeedRepo


    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        userStore = mock {
            on { token } doReturn "token"
        }

        messageMapper = mock {
            on { fromJson(any()) } doReturn message
        }
        minMaxMapper = mock {
            on { fromJson(any()) } doReturn minMax
        }
        monthlyReportMapper = mock {
            on { fromJson(any()) } doReturn monthlyReport
        }
        newsMapper = mock {
            on { fromJson(any()) } doReturn news
        }
        tipMapper = mock {
            on { fromJson(any()) } doReturn tip
        }
        weeklyReportMapper = mock {
            on { fromJson(any()) } doReturn weeklyReport
        }

        costAnalysisMapper = mock {
            on { fromJson(any()) } doReturn costAnalysisFeedItem
        }

        predictedSavingsMapper= mock {
            on { fromJson(predictedInvoiceJson = predictedInvoiceJson) } doReturn predictedInvoiceItem
            on { fromJson(predictedYearlySavingsJson = predictedYearlySavingJson) } doReturn predictedYearlySavingsFeedItem

        }

        repo = FeedRepo(
                retrofit.create(GreenelyApi::class.java),
                userStore,
                messageMapper,
                minMaxMapper,
                monthlyReportMapper,
                newsMapper,
                tipMapper,
                weeklyReportMapper,
                costAnalysisMapper,
                predictedSavingsMapper
        )
    }

    @Test
    fun testFeed() {
        // Given
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; chartset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "feed_messages": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "Message title",
                                                    "text": "Message text",
                                                    "new_entry": false
                                                }
                                            ],
                                            "day_high_lows": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "Min max title",
                                                    "text": "Min max text",
                                                    "new_entry": false
                                                }
                                            ],
                                            "month_availables": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "Monthly report title",
                                                    "text": "Monthly report text",
                                                    "new_entry": false,
                                                    "chart_data": [
                                                        {
                                                            "timestamp": 1483228800,
                                                            "usage": 25730
                                                        },
                                                        {
                                                            "timestamp": 1483315200,
                                                            "usage": 24130
                                                        },
                                                        {
                                                            "timestamp": 1483401600,
                                                            "usage": null
                                                        }
                                                    ]
                                                }
                                            ],
                                            "feed_tips": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "Tips title",
                                                    "text": "Tips text",
                                                    "new_entry": true
                                                }
                                            ],
                                            "feed_news": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "News title",
                                                    "text": "News text",
                                                    "new_entry": true
                                                }
                                            ],
                                            "week_availables": [
                                                {
                                                    "created": 1483228800,
                                                    "title": "Monthly report title",
                                                    "text": "Monthly report text",
                                                    "new_entry": false,
                                                    "week_chart_data": []
                                                }
                                            ]
                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        // When
        val feed = repo.getFeed().blockingFirst()
        val request = server.takeRequest(0, TimeUnit.MILLISECONDS)

        // Then
        assertThat(request).isNotNull()
        assertThat(request.requestLine).isEqualTo("GET /v1/feed/entries HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore, times(1)).token = "token"

        verify(messageMapper).fromJson(MessageJson(
                1483228800,
                "Message title",
                "Message text",
                false
        ))

        assertThat(feed).isNotNull
        assertThat(feed).isEqualTo(listOf(message, minMax, monthlyReport, news, tip, weeklyReport))
    }

    @Test
    fun testFeed_error() {
        server.enqueue(
                MockResponse().setResponseCode(400)
        )

        var error: Throwable? = null
        repo.getFeed().subscribeBy(onError = { error = it })

        assertThat(error).isNotNull()
        assertThat(error).isInstanceOf(HttpException::class.java)
    }

    @Test
    fun testHasUnreadItems() {
        // Given
        server.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "data": {
                    "count": 1
                },
                "jwt": "token"
            }
        """.trimIndent()))

        // When
        val response = repo.hasUnreadItems().blockingFirst()

        // Then
        assertThat(response).isTrue()
    }

    @Test
    fun testHasNoUnreadItems() {
        // Given
        server.enqueue(MockResponse().setResponseCode(200).setBody("""
            {
                "data": {
                    "count": 0
                },
                "jwt": "token"
            }
        """.trimIndent()))

        // When
        val response = repo.hasUnreadItems().blockingFirst()

        // Then
        assertThat(response).isFalse()
    }

    @Test
    fun testHasUnreadItemsError() {
        // Given
        server.enqueue(MockResponse().setResponseCode(400))

        // When
        val response = repo.hasUnreadItems().blockingFirst()

        // Then
        assertThat(response).isFalse()
    }
}

