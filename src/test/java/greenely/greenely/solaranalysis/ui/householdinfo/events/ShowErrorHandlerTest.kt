package greenely.greenely.solaranalysis.ui.householdinfo.events

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.errors.ErrorHandler
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ShowErrorHandlerTest {
    private lateinit var errorHandler: ErrorHandler
    private lateinit var next: EventHandler
    private lateinit var handler: ShowErrorHandler

    @Before
    fun setUp() {
        errorHandler = mock()
        next = mock()
        handler = ShowErrorHandler(errorHandler, next)
    }

    @Test
    fun testHandleEvent() {
        // Given
        val error = IOException()
        val event = Event.ShowError(error)

        // When
        handler.handleEvent(event)

        // Then
        verify(errorHandler).handleError(error)
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