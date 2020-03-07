package greenely.greenely.home.ui.events

import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class ShowLoadingHandlerTest {
    private lateinit var view: View
    private lateinit var scrollView: View
    private lateinit var next: EventHandler
    private lateinit var handler: LoadingHandler

    @Before
    fun setUp() {
        view = mock()
        scrollView = mock()
        next = mock()
        handler = LoadingHandler(view, scrollView, next)
    }

    @Test
    fun testShowLoader() {
        // When
        handler.handleEvent(Event.LoaderUIEvent(true))

        // Then
        verify(view).visibility = View.VISIBLE
        verify(scrollView).visibility = View.INVISIBLE
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testHideLoader() {
        // When
        handler.handleEvent(Event.LoaderUIEvent(false))

        // Then
        verify(view).visibility = View.GONE
        verify(scrollView).visibility = View.VISIBLE
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