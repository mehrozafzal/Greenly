package greenely.greenely.errors.snackbar

import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.HasSnackbarView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.io.IOException

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Snackbar::class)
class IOExceptionHandlerTest {
    private lateinit var view: View
    private lateinit var errorHandler: IOExceptionHandler
    private lateinit var next: ErrorHandler
    private lateinit var snackbar: Snackbar
    private lateinit var hasSnackbarView: HasSnackbarView

    @Before
    fun setUp() {
        next = mock()
        view = mock()
        snackbar = mock()
        hasSnackbarView = mock {
            on { snackbarView } doReturn view
        }

        PowerMockito.mockStatic(Snackbar::class.java)
        PowerMockito.`when`(Snackbar.make(view, R.string.network_error_body, Snackbar.LENGTH_LONG))
                .thenReturn(snackbar)

        errorHandler = IOExceptionHandler(hasSnackbarView)
        errorHandler.next = next
    }

    @Test
    fun testHandleError() {
        // Given
        val error = IOException()

        // When
        errorHandler.handleError(error)

        // Then
        verify(snackbar).show()
        verify(next, never()).handleError(any())
    }

    @Test
    fun testUnhandled() {
        // Given
        val error = Error()

        // When
        errorHandler.handleError(error)

        // Then
        verify(next).handleError(error)
        verify(snackbar, never()).show()
    }
}