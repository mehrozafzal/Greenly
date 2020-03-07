@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.settings

import com.nhaarman.mockito_kotlin.doReturn
import greenely.greenely.api.GreenelyApi
import greenely.greenely.settings.data.Household
import greenely.greenely.settings.data.NotificationMapper
import greenely.greenely.settings.data.SettingsInfo
import greenely.greenely.settings.data.SettingsRepo
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

class SettingsRepoTest {
    @Rule
    @JvmField
    val server = MockWebServer()

    private lateinit var userStore: UserStore
    private lateinit var repo: SettingsRepo
    private lateinit var notificationMapper: NotificationMapper

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(server.url("/"))
                .build()


        userStore = com.nhaarman.mockito_kotlin.mock {
            on { token } doReturn "token"
        }


        notificationMapper= io.kotlintest.mock.mock()
        repo = SettingsRepo(retrofit.create(GreenelyApi::class.java), userStore,notificationMapper)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testGetSettingsInfo() {
        val responseBody = """
            {
                "data": {
                    "account": {
                        "has_poa": false,
                        "email": "test@test.com"
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

        var error: Throwable? = null
        var response: SettingsInfo? = null
        repo.getSettingsInfo()
                .subscribeBy(
                        onNext = {
                            response = it
                        },
                        onError = {
                            error = it
                        }
                )

        val request = server.takeRequest()

        assertThat(request.requestLine).isEqualTo("GET /v1/settings HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        assertThat(error).isNull()
        assertThat(response).isNotNull()

        verify(userStore, times(1)).token = "token"

        assertThat(response).isEqualTo(SettingsInfo(false, "test@test.com"))
    }

    @Test
    fun testGetSettingsInfo_error() {
        server.enqueue(
                MockResponse().setResponseCode(400)
        )

        var error: Throwable? = null
        var response: SettingsInfo? = null
        repo.getSettingsInfo()
                .subscribeBy(
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
    fun testGetHousehold() {
        val responseBody = """
            {
                "data": {
                    "municipality": "Stockholm",
                    "facility_type": "Villa",
                    "heating_type": "Fjärrvärme",
                    "secondary_heating_type": "Fjärrvärme",
                    "tertiary_heating_type": "Fjärrvärme",
                    "quaternary_heating_type": "Fjärrvärme",
                    "electric_car_count": "2",
                    "occupants": "6",
                    "construction_year": "1975",
                    "facility_area": "145-150",
                    "meter": "Not set"
                },
                "jwt": "token"
            }
        """.trimIndent()
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(responseBody)
        )


        var error: Throwable? = null
        var response: Household? = null
        repo.getHousehold()
                .subscribeBy(
                        onNext = {
                            response = it
                        },
                        onError = {
                            error = it
                        }
                )

        val request = server.takeRequest()

        assertThat(request.requestLine).isEqualTo("GET /v2/settings/household HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        assertThat(error).isNull()
        assertThat(response).isNotNull()
        assertThat(response).isEqualTo(
                Household(
                        "Stockholm",
                        "Not set",
                        "145-150",
                        "Villa",
                        "Fjärrvärme",
                        "Fjärrvärme",
                        "Fjärrvärme",
                        "Fjärrvärme",
                        "2",
                        "6",
                        "1975"

                )
        )
        verify(userStore).token = "token"
    }

    @Test
    fun testGetHousehold_error() {
        server.enqueue(
                MockResponse().setResponseCode(400)
        )

        `when`(userStore.token).thenReturn("token")

        var error: Throwable? = null
        var response: Household? = null
        repo.getHousehold()
                .subscribeBy(
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
    fun testChangePassword() {
        val responseBody = """
            {
                "jwt": "token"
            }
        """.trimIndent()
        server.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(responseBody)
        )

        `when`(userStore.token).thenReturn("token")

        var error: Throwable? = null
        var response: Boolean? = null
        repo.changePassword("oldPassword", "newPassword")
                .subscribeBy(
                        onNext = {
                            response = it
                        },
                        onError = {
                            error = it
                        }
                )

        val request = server.takeRequest()
        assertThat(request.requestLine).isEqualTo("POST /v1/settings/changepassword HTTP/1.1")
        assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        assertThat(request.body.readUtf8()).isEqualToIgnoringWhitespace(
                """
                    {
                        "new_password": "newPassword",
                        "old_password": "oldPassword"
                    }
                """.trimIndent()
        )

        verify(userStore, times(1)).token = "token"

        assertThat(error).isNull()
        assertThat(response).isNotNull()
        assertThat(response).isTrue()
    }

    @Test
    fun testChangePassword_error() {
        server.enqueue(
                MockResponse().setResponseCode(400)
        )

        `when`(userStore.token).thenReturn("token")

        var error: Throwable? = null
        var response: Boolean? = null
        repo.changePassword("oldPassword", "newPassword")
                .subscribeBy(
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

