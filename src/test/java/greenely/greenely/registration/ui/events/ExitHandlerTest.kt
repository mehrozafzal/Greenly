package greenely.greenely.registration.ui.events

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.registration.ui.RegistrationActivity
import org.junit.Before
import org.junit.Test

class ExitHandlerTest {
    private lateinit var activity: RegistrationActivity
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
}