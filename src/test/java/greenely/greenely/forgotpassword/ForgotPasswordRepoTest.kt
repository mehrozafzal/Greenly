@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.forgotpassword

import greenely.greenely.api.GreenelyApi
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.operators.completable.CompletableToObservable
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

class ForgotPasswordRepoTest {

    private lateinit var repo: ForgotPasswordRepo

    @Rule
    @JvmField
    val server = MockWebServer()

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        repo = ForgotPasswordRepo(retrofit.create(GreenelyApi::class.java))
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    @Test
    fun testSendEmail() {
        server.enqueue(
                MockResponse()
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .setBody("{}")
        )

        repo.sendEmail("anton.holmberg@greenely.se").subscribe()

        val recordedRequest = server.takeRequest()

        assertThat(recordedRequest.requestLine)
                .isEqualTo("POST /v1/reset-password HTTP/1.1")
        assertThat(recordedRequest.getHeader("Content-Type"))
                .isEqualToIgnoringCase("application/json; charset=utf-8")
        assertThat(recordedRequest.body.readUtf8()).isEqualToIgnoringWhitespace(
                """
                    {
                        "recovery": "anton.holmberg@greenely.se"
                    }
                """.trimIndent()
        )
    }

    @Test
    fun testError() {
        server.enqueue(
                MockResponse()
                        .addHeader("Content-Type", "application/json; charset=UTF-8")
                        .setResponseCode(400)
                        .setBody(
                                """
                                    {
                                        "error": "I am an error"
                                    }
                                """.trimIndent()
                        )
        )

        var error: Throwable? = null

        CompletableToObservable<Any>(repo.sendEmail("anton.holmberg@greenely.se"))
                .subscribeBy(
                        onError = {
                            error = it
                        }
                )

        assertThat(error).isInstanceOf(HttpException::class.java)
    }
}

