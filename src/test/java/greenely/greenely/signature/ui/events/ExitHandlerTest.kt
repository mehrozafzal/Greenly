package greenely.greenely.signature.ui.events

import com.nhaarman.mockito_kotlin.*
import greenely.greenely.signature.ui.SignatureActivity
import org.junit.Before
import org.junit.Test

class ExitHandlerTest {
    private lateinit var activity: SignatureActivity
    private lateinit var next: EventHandler
    private lateinit var handler: ExitHandler

    @Before
    fun setUp() {
        activity = mock()
        next = mock()

        handler = ExitHandler(activity, next)
    }

    @Test
    fun testHandleEvent() {
        // Given
        val event = Event.Exit

        // When
        handler.handleEvent(event)

        // Then
        verify(activity).finish()
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testUnhandled() {
        // Given
        val event = mock<Event>()

        // When
        handler.handleEvent(event)

        // Then
        verify(next).handleEvent(event)
    }
}