package greenely.greenely.retail.ui.events

import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class LoadingHandlerTest {
    private lateinit var loader: View
    private lateinit var next: EventHandler
    private lateinit var handler: greenely.greenely.retail.ui.events.LoadingHandler

    @Before
    fun setUp() {
        loader = mock()
        next = mock()
        handler = LoadingHandler(loader)
    }

    @Test
    fun testShowLoader() {
        // When
        handler.handleEvent(Event.LoaderUIEvent(true))

        // Then
        verify(loader).visibility = View.VISIBLE
        verify(next, never()).handleEvent(any())
    }

    @Test
    fun testHideLoader() {
        // When
        handler.handleEvent(Event.LoaderUIEvent(false))

        // Then
        verify(loader).visibility = View.GONE
        verify(next, never()).handleEvent(any())
    }

}