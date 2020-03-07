package greenely.greenely.main.events

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.tracking.Tracker
import org.junit.Before
import org.junit.Test

class LogoutHandlerTest {
    private lateinit var tracker: Tracker
    private lateinit var activity: MainActivity
    private lateinit var next: EventHandler
    private lateinit var handler: LogoutHandler

    @Before
    fun setUp() {
        tracker = mock()
        activity = mock()
        next = mock()

        handler = LogoutHandler(activity, tracker, next)
    }

    @Test
    fun testHandleEvent() {
        // Given
        val event = Event.Logout

        // When
        handler.handleEvent(event)

        // Then
        verify(tracker).reset()
        verify(activity).finish()
        verify(next, never()).handleEvent(any())
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