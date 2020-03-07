@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.home.data

import com.squareup.moshi.Moshi
import greenely.greenely.R
import greenely.greenely.api.GreenelyApi
import greenely.greenely.home.models.HomeResponse
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
import org.mockito.Mockito.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class HomeRepoTest {
    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var userStore: UserStore
    private lateinit var repo: HomeRepo

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(
                        Moshi.Builder()
                                .add(HomeResponseAdapter())
                                .add(DataStateAdapter())
                                .build()
                ))
                .build()

        userStore = mock(UserStore::class.java)
        repo = HomeRepo(retrofit.create(GreenelyApi::class.java), userStore)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testGetHome() {
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                        "data": {
                                            "points": [
                                                    {
                                                        "timestamp": 1496275200,
                                                        "others": 135000,
                                                        "best": 107000
                                                    }
                                            ],
                                            "state": 1,
                                            "resolution": "D",
                                            "comparison_max_value": 135000.0,
                                            "points_max_value": 135000.0,
                                            "comparison_info_title": "You are compared to 42 households.",
                                            "feedback_text": "Your household used 0% less than comparable top households.",
                                            "comparison_info_text": "Lägenhet, direktverkande el, 3 occupants, 90-109 m2."

                                        },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        `when`(userStore.token).thenReturn("token")

        var response: HomeResponse? = null
        var error: Throwable? = null
        repo.getHome(R.id.days).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine).isEqualTo("GET /v4/home?resolution=D HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        verify(userStore).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response).isEqualTo(
                HomeResponse(
                        DataState.NEEDS_POA,
                        listOf(Comparison(
                                1496275200,
                                null,
                                135000,
                                107000
                        )
                        ),
                        "Your household used 0% less than comparable top households.",
                        "You are compared to 42 households.",
                        "Lägenhet, direktverkande el, 3 occupants, 90-109 m2.",
                        "D",
                        Comparison(
                                1496275200,
                                null,
                                135000,
                                107000
                        ),
                        135f,
                        135f,0,null,0f,null
                )
        )
    }


    @Test
    fun testGetHome_error() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        `when`(userStore.token).thenReturn("token")

        var response: HomeResponse? = null
        var error: Throwable? = null
        repo.getHome(R.id.days).subscribeBy(
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
}