package greenely.greenely.errors.alert

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.stepstone.stepper.VerificationError
import greenely.greenely.R
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.exceptions.VerificationException
import org.junit.Before
import org.junit.Test

class VerificationExceptionHandlerTest {
    private lateinit var activity: Activity
    private lateinit var builder: AlertDialog.Builder
    private lateinit var next: ErrorHandler
    private lateinit var handler: VerificationExceptionHandler

    @Before
    fun setUp() {
        activity = mock()
        builder = mock()
        next = mock()

        handler = VerificationExceptionHandler(activity, next, builder)
    }

    @Test
    fun testHandleError() {
        // Given
        val error = VerificationError("Error")

        // When
        handler.handleError(VerificationException(error))

        // Then
        val onClick = argumentCaptor<DialogInterface.OnClickListener>()

        verify(builder).setTitle(R.string.invalid_input)
        verify(builder).setMessage(error.errorMessage)
        verify(builder).setPositiveButton(eq(R.string.okay), onClick.capture())
        verify(builder).show()

        val dialog = mock<DialogInterface>()
        onClick.lastValue.onClick(dialog, -1)

        verify(dialog).dismiss()
    }

    @Test
    fun testNext() {
        // Given
        val error: Throwable = mock()

        // When
        handler.handleError(error)

        // Then
        verify(next).handleError(error)
    }
}