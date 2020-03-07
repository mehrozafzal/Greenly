package greenely.greenely.login.ui.events

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.login.ui.LoginActivity
import org.junit.Before
import org.junit.Test

class ForgotPasswordHandlerTest {
    private lateinit var activity: LoginActivity
    private lateinit var next: EventHandler
    private lateinit var handler: ForgotPasswordHandler

    @Before
    fun setUp() {
        activity = mock()
        next = mock()
        handler = ForgotPasswordHandler(activity, next)
    }

    @Test
    fun testHandleEvent() {
        // Given
        val event = Event.ForgotPassword

        // When
        handler.handleEvent(event)

        // Then
        verify(activity).startActivity(any())
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testUnhandled() {
        // Given
        val event = Event.Exit

        // When
        handler.handleEvent(event)

        // Then
        verify(next).handleEvent(event)
    }
}