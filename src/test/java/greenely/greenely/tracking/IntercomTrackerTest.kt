@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.tracking

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.segment.analytics.Properties
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.identity.Registration
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Intercom::class)
class IntercomTrackerTest {

    private lateinit var intercom: Intercom
    private lateinit var wrapped: Tracker
    private lateinit var tracker: IntercomTracker

    @Before
    fun setUp() {
        intercom = mock()
        wrapped = mock()

        PowerMockito.mockStatic(Intercom::class.java)
        PowerMockito.`when`(Intercom.client()).thenReturn(intercom)

        tracker = IntercomTracker(wrapped)
    }

    @Test
    fun testTrackScreen() {
        // When
        tracker.trackScreen("Screen")

        // Then
        verify(wrapped).trackScreen("Screen")
    }

    @Test
    fun testIdentify() {
        // Given
        val identifier = Tracker.UserIdentifier(1, "hash", "email", mapOf(Pair("muncipality", "stockholm")))

        // When
        tracker.identify(identifier)

        // Then
        val registrationCaptor = argumentCaptor<Registration>()

        verify(wrapped).identify(identifier)

        verify(intercom).setUserHash(identifier.userHash)
        verify(intercom).registerIdentifiedUser(registrationCaptor.capture())

        val registration = registrationCaptor.lastValue
        assertThat(registration.userId).isEqualTo(identifier.userId.toString())
    }

    @Test
    fun testTrack() {
        // Given
        val properties = mock<Properties>()
        val event = mock<Tracker.Event> {
            on { name } doReturn "Not Snake Case"
            on { this.properties } doReturn properties
        }

        // When
        tracker.track(event)

        // Then
        verify(wrapped).track(event)
    }
}

