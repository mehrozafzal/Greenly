package greenely.greenely.retailonboarding.data

import androidx.databinding.ObservableBoolean
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.squareup.moshi.Moshi
import greenely.greenely.api.GreenelyApi
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retailonboarding.mappers.CustomerInfoMapper
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import greenely.greenely.store.UserStore
import greenely.greenely.utils.NonNullObservableField
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class RetailOnboardingRepoTest {

    @Rule
    @JvmField
    val mockServer = MockWebServer()

    private lateinit var userStore: UserStore
    private lateinit var repo: RetailOnboardingRepo
    private lateinit var customerInfoMapper: CustomerInfoMapper

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.from { it.run() } }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.from { it.run() } }

        val retrofit = Retrofit.Builder()
                .baseUrl(mockServer.url("/"))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(
                        Moshi.Builder()
                                .build()
                ))
                .build()

        customerInfoMapper = CustomerInfoMapper()

        userStore = mock {
            on { token } doReturn "token"
        }

        repo = RetailOnboardingRepo(
                retrofit.create(GreenelyApi::class.java),
                userStore,
                customerInfoMapper)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testSendCustomerInfo() {
        //Given
        val customerInfo = CustomerInfoModel(
                personalNumber = NonNullObservableField("19930919-1238"),
                address = NonNullObservableField("Test street"),
                postalCode = NonNullObservableField("123456"),
                postalRegion = NonNullObservableField("12345"),
                email = NonNullObservableField("test@greenely.se"),
                phoneNumber = NonNullObservableField("0712345678"),
                userTermsAccepted = ObservableBoolean(true),
                powerOfAttorneyTermsAccepted = ObservableBoolean(true)
        )

        mockServer.enqueue(
                MockResponse()
                        .setBody("""
                            {
                                "data": {
                                    "bankid_order_ref": "130553f7-898e-4719-b384-16732896e729",
                                    "bankid_start_token": "f565e6f0-953c-4c1c-b96a-f20d405d38ef"
                                },
                                "jwt": "token"
                            }
                        """.trimIndent()))

        // When
        val response = repo.sendCustomerInfo(customerInfo).blockingFirst()

        // Then
        val request = mockServer.takeRequest()

        verify(userStore).token = "token"
        Assertions.assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")
        Assertions.assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        Assertions.assertThat(request.requestLine).isEqualTo("POST /v1/retail/convert HTTP/1.1")
        Assertions.assertThat(response.bankidOrderRef).isEqualTo("130553f7-898e-4719-b384-16732896e729")
        Assertions.assertThat(response.bankidStartToken).isEqualTo("f565e6f0-953c-4c1c-b96a-f20d405d38ef")
    }

    @Test
    fun testGetBankIdProcessStatus() {
        //Given
        mockServer.enqueue(
                MockResponse()
                        .setHeader("Content-Type", "application/json; charset=UTF-8")
                        .setBody(
                                """
                                    {
                                    "data": {
                                        "bankid_status": "COMPLETED",
                                        "bankid_title": null,
                                        "bankid_message": "Message"
                                    },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        //When
        Mockito.`when`(userStore.token).thenReturn("token")

        //Then
        var response: RetailBankIdProcessJson? = null
        var error: Throwable? = null
        repo.getBankIdProcessStatus("").subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        val request = mockServer.takeRequest()

        Assertions.assertThat(request.requestLine).isEqualTo("GET /v1/retail/bankid?bankid_order_ref= HTTP/1.1")
        Assertions.assertThat(request.getHeader("Authorization")).isEqualTo("JWT token")

        Mockito.verify(userStore).token = "token"

        Assertions.assertThat(error).isNull()
        Assertions.assertThat(response).isNotNull()

        Assertions.assertThat(response?.bankIdStatus).isEqualTo("COMPLETED")
        Assertions.assertThat(response?.bankIdMessage).isEqualTo("Message")
    }

    @Test
    fun testGetBankIdProcessStatusError() {
        //Given
        mockServer.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody("{}")
        )

        //When
        Mockito.`when`(userStore.token).thenReturn("token")

        //Then
        var response: RetailBankIdProcessJson? = null
        var error: Throwable? = null
        repo.getBankIdProcessStatus("").subscribeBy(
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
    fun testCancelExistingBankIdProcess() {
        //Given
        mockServer.enqueue(
                MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                """
                                    {
                                    "data": {
                                    },
                                        "jwt": "token"
                                    }
                                """.trimIndent()
                        )
        )

        //When
        Mockito.`when`(userStore.token).thenReturn("token")

        //Then
        var response: RetailBankIdProcessJson? = null
        var error: Throwable? = null
        repo.cancelExistingBankIdProcess("").subscribeBy(
                onNext = {
                    response = it
                },
                onError = {
                    error = it
                }
        )

        Assertions.assertThat(error).isNull()
        Assertions.assertThat(response).isNotNull
    }
}