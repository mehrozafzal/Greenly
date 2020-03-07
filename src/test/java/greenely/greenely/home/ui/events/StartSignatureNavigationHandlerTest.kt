package greenely.greenely.home.ui.events

import android.app.Activity
import android.content.Intent
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.signature.ui.SignatureActivity
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

class StartSignatureNavigationHandlerTest {
    private lateinit var activity: Activity
    private lateinit var fragment: HomeFragment
    private lateinit var next: EventHandler
    private lateinit var handler: StartSignatureNavigationHandler

    @Before
    fun setUp() {
        activity = mock()
        fragment = HomeFragment()
        next = mock()

        handler = StartSignatureNavigationHandler(fragment, activity)
        handler.next = next
    }

    @Test
    fun testHandleEvent() {
        // When
        handler.handleEvent(Event.StartSignatureNavigationEvent(MainActivity.SIGN_POA_REQUEST))

        // Then
        val intentCaptor = argumentCaptor<Intent>()

        verify(next, never()).handleEvent(any())
        verify(activity).startActivityForResult(intentCaptor.capture(), eq(MainActivity.SIGN_POA_REQUEST))

        Assertions.assertThat(intentCaptor.firstValue.component)
                .isEqualTo(Intent(activity, SignatureActivity::class.java).component)
    }

    @Test
    fun testNext() {
        // Given
        val event: Event = mock()

        // When
        handler.handleEvent(event)

        // Then
        verify(next).handleEvent(event)
    }
}
