@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.login

import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.api.GreenelyApi
import greenely.greenely.login.data.LoginRepo
import greenely.greenely.login.model.LoginData
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.IntercomInfo
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

class LoginRepoTest {

    @Rule
    @JvmField
    val server = MockWebServer()

    lateinit var userStore: UserStore

    lateinit var repo: LoginRepo

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        userStore = mock(UserStore::class.java)

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        repo = LoginRepo(retrofit.create(GreenelyApi::class.java), userStore)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testLogin() {
        val responseBody = """
                            {
                                "data": {
                                    "user_id": 1,
                                    "account_setup_next_v3": "HOUSEHOLD",
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
                            """.trimIndent()

        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(responseBody)
        )

        var response: AuthenticationInfo? = null
        var error: Throwable? = null
        repo.login(LoginData("anton.holmberg@greenely.se", "password")).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("POST /v1/login HTTP/1.1")
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.body.readUtf8()).isEqualToIgnoringWhitespace(
                """
                    {
                        "email": "anton.holmberg@greenely.se",
                        "password": "password"
                    }
                """.trimIndent()
        )

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response).isEqualTo(
                AuthenticationInfo(
                        1,
                        AccountSetupNext.HOUSEHOLD,
                        mapOf(
                                Pair("municipality", "stockholm"),
                                Pair("facility_type", "villa"),
                                Pair("heating_type", "heatingType"),
                                Pair("secondary_heating_type", ""),
                                Pair("tertiary_heating_type", ""),
                                Pair("quaternary_heating_type", ""),
                                Pair("facility_area", "20-90"),
                                Pair("occupants", "4"),
                                Pair("construction_year", "1900"),
                                Pair("electric_car_count", "2")
                        ),
                        IntercomInfo(
                                "1",
                                "hash",
                                "anton.holmberg@greenely.se"
                        )
                )
        )
    }

    @Test
    fun testLogin_error() {
        server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        var response: AuthenticationInfo? = null
        var error: Throwable? = null
        repo.login(LoginData("anton.holmberg@greenely.se", "password")).subscribeBy(
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
    fun testLogin_noRetrofit() {
        val responseBody = """
                            {
                                "data": {
                                    "user_id": 1,
                                    "account_setup_next_v3": "HOUSEHOLD",
                                    "properties": {}
                                },
                                "jwt": "token"
                            }
                            """.trimIndent()

        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(responseBody)
        )

        var response: AuthenticationInfo? = null
        var error: Throwable? = null
        repo.login(LoginData("anton.holmberg@greenely.se", "password")).subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = server.takeRequest()

        assertThat(request.requestLine)
                .isEqualTo("POST /v1/login HTTP/1.1")
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.body.readUtf8()).isEqualToIgnoringWhitespace(
                """
                    {
                        "email": "anton.holmberg@greenely.se",
                        "password": "password"
                    }
                """.trimIndent()
        )

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        assertThat(response).isEqualTo(
                AuthenticationInfo(
                        1,
                        AccountSetupNext.HOUSEHOLD,
                        mapOf(),
                        null
                )
        )
    }
}

