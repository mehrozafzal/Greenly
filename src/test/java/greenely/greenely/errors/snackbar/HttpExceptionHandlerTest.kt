package greenely.greenely.errors.snackbar

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.view.View
import com.nhaarman.mockito_kotlin.*
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.HasSnackbarView
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import retrofit2.HttpException
import retrofit2.Response

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Snackbar::class)
class HttpExceptionHandlerTest {
    private lateinit var view: View
    private lateinit var errorHandler: HttpExceptionHandler
    private lateinit var next: ErrorHandler
    private lateinit var snackbar: Snackbar
    private lateinit var hasSnackbarView: HasSnackbarView

    @Before
    fun setUp() {
        next = mock()
        val context = mock<Context> {
            on { getString(R.string.unexpected_error_body) } doReturn "Unexpected error"
        }
        view = mock {
            on { this.context } doReturn context
        }
        snackbar = mock()
        hasSnackbarView = mock {
            on { snackbarView } doReturn view
        }

        PowerMockito.mockStatic(Snackbar::class.java)

        errorHandler = HttpExceptionHandler(hasSnackbarView)
        errorHandler.next = next
    }

    @Test
    fun testHandleError() {
        // Given
        PowerMockito
                .`when`(Snackbar.make(view, "Body", Snackbar.LENGTH_LONG))
                .thenReturn(snackbar)
        val error = HttpException(createErrorResponse("Title", "Body"))

        // When
        errorHandler.handleError(error)

        // Then
        verify(snackbar).show()
        verify(next, never()).handleError(any())
    }

    @Test
    fun testWithServerError() {
        // Given
        PowerMockito
                .`when`(Snackbar.make(view, "Unexpected error", Snackbar.LENGTH_LONG))
                .thenReturn(snackbar)
        val error = HttpException(Response.error<Any>(500, ResponseBody.create(
                MediaType.parse("application/json"),
                ""
        )))

        // When
        errorHandler.handleError(error)

        // Then
        verify(snackbar).show()
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

    private fun createErrorResponse(title: String, description: String): Response<Any> {
        return Response.error(400, ResponseBody.create(
                MediaType.parse("application/json"),
                """
                    |{
                    |   "title": "$title",
                    |   "description": "$description"
                    |}
                """.trimMargin("|")
        ))
    }
}