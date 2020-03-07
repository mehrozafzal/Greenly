@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.utils

import androidx.core.widget.ContentLoadingProgressBar
import android.view.View
import android.view.ViewTreeObserver
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

class OverlappingLoaderHandlerTest {
    private lateinit var loader: ContentLoadingProgressBar
    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener
    private lateinit var content: View
    private lateinit var loadingHandler: OverlappingLoaderHandler

    @Before
    fun setUp() {
        val viewTreeObserver = mock<ViewTreeObserver>()
        val globalLayoutCaptor = argumentCaptor<ViewTreeObserver.OnGlobalLayoutListener>()

        loader = mock {
            on { this.viewTreeObserver } doReturn viewTreeObserver
            on { visibility } doReturn -1
        }
        content = mock()

        loadingHandler = OverlappingLoaderHandler(loader, content)
        verify(viewTreeObserver).addOnGlobalLayoutListener(globalLayoutCaptor.capture())

        globalLayoutListener = globalLayoutCaptor.firstValue
    }

    @Test
    fun testLoading() {
        // When
        loadingHandler.loading = true

        // Then
        verify(loader).show()
    }

    @Test
    fun testStopLoadingNotVisible() {
        // Given
        loader.stub {
            on { visibility } doReturn View.GONE
        }

        // When
        loadingHandler.loading = false

        // Then
        verify(loader).hide()
        verify(content).visibility = View.VISIBLE
    }

    @Test
    fun testStopLoadingVisible() {
        // Given
        loader.stub {
            on { visibility } doReturn View.VISIBLE
        }

        // When
        loadingHandler.loading = false

        // Then
        verify(loader).hide()
        verify(content, never()).visibility = View.VISIBLE
    }

    @Test
    fun testLayoutUpdatedVisible() {
        // Given
        loader.stub {
            on { visibility } doReturn View.VISIBLE
        }

        // When
        globalLayoutListener.onGlobalLayout()

        // Then
        verify(content).visibility = View.INVISIBLE
    }

    @Test
    fun testLayoutUpdateGone() {
        // Given
        loader.stub {
            on { visibility } doReturn View.GONE
        }

        // When
        globalLayoutListener.onGlobalLayout()

        // Then
        verify(content).visibility = View.VISIBLE
    }
}

