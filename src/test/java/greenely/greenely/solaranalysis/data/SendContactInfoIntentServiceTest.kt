package greenely.greenely.solaranalysis.data

import android.content.Intent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import greenely.greenely.R
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.utils.NonNullObservableField
import io.reactivex.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(
        packageName = "greenely.greenely",
        application = TestApplication::class,
        sdk = [21]
)
class SendContactInfoIntentServiceTest {
    private lateinit var service: SendContactInfoIntentService

    @Before
    fun setUp() {
        DaggerTestComponent.builder()
                .viewModelProviderFactory(mock())
                .build()
                .inject(RuntimeEnvironment.application as TestApplication)


        service = Robolectric.setupService(SendContactInfoIntentService::class.java)
    }

    @Test
    fun testHandleIntent() {
        // Given
        service.repo.stub {
            on { sendContactInformation(any(), any()) } doReturn Completable.complete()
        }

        val analysis = Analysis(
                "12",
                "12",
                "12",
                "12",
                "12",
                "12",
                "12",
                "12",
                mutableListOf())

        val contactInfo = ContactInfo(
                NonNullObservableField("Name"),
                NonNullObservableField("Email"),
                NonNullObservableField("070 000 00 00"))
        val intent = Intent().apply {
            putExtra("analysis", analysis)
            putExtra("contactInfo", contactInfo)
        }

        // When
        service.onHandleWork(intent)

        // Then
        assertThat(ShadowToast.getTextOfLatestToast())
                .isEqualTo(service.getString(R.string.contact_information_sent))

        val broadcast = Shadows.shadowOf(service).broadcastIntents.first()
        assertThat(broadcast.action).isEqualTo(service.getString(R.string.CONTACT_INFO_SENT))
    }

    @Test
    fun testErrorFromServer() {
        // Given
        service.repo.stub {
            on { sendContactInformation(any(), any()) } doReturn Completable.error(Error())
        }

        val analysis = Analysis("", "", "", "", "", "", "", "", mutableListOf())
        val contactInfo = ContactInfo()
        val intent = Intent().apply {
            putExtra("analysis", analysis)
            putExtra("contactInfo", contactInfo)
        }

        // When
        service.onHandleWork(intent)

        // Then
        assertThat(ShadowToast.getTextOfLatestToast())
                .isEqualTo(service.getString(R.string.could_not_send_contact_information))
    }

    @Test
    fun testMissingDataError() {
        // When
        service.onHandleWork(Intent())

        // Then
        assertThat(ShadowToast.getTextOfLatestToast())
                .isEqualTo(service.getString(R.string.could_not_send_contact_information))
    }
}