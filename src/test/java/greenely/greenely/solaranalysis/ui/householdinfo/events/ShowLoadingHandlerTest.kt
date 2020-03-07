package greenely.greenely.solaranalysis.ui.householdinfo.events

import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class ShowLoadingHandlerTest {
    private lateinit var view: View
    private lateinit var next: EventHandler
    private lateinit var handler: ShowLoadingHandler

    @Before
    fun setUp() {
        view = mock()
        next = mock()
        handler = ShowLoadingHandler(view, next)
    }

    @Test
    fun testShowLoader() {
        // When
        handler.handleEvent(Event.ShowLoader(true))

        // Then
        verify(view).visibility = View.VISIBLE
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testHideLoader() {
        // When
        handler.handleEvent(Event.ShowLoader(false))

        // Then
        verify(view).visibility = View.GONE
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