package greenely.greenely.errors.alert

import android.widget.Button
import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.EmptyActivity
import greenely.greenely.R
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import greenely.greenely.errors.ErrorHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely",
        application = TestApplication::class
)
class IOExceptionHandlerTest {

    private lateinit var activity: EmptyActivity
    private lateinit var errorHandler: IOExceptionHandler
    private lateinit var next: ErrorHandler

    @Before
    fun setUp() {
        next = mock()

        DaggerTestComponent.builder().viewModelProviderFactory(mock()).build().inject(
                RuntimeEnvironment.application as TestApplication
        )

        activity = Robolectric.setupActivity(EmptyActivity::class.java)

        errorHandler = IOExceptionHandler(activity)
        errorHandler.next = next
    }

    @Test
    fun testSetUp() {
        assertThat(activity).isNotNull()
        assertThat(errorHandler).isNotNull()
        assertThat(next).isNotNull()
    }

    @Test
    fun testHandleError() {
        // Given
        val error = IOException()

        // When
        errorHandler.handleError(error)

        // Then
        val dialog = ShadowDialog.getLatestDialog()

        assertThat(dialog.findViewById<TextView>(R.id.alertTitle).text)
                .isEqualTo(activity.getString(R.string.network_error_title))
        assertThat(dialog.findViewById<TextView>(android.R.id.message).text)
                .isEqualTo(activity.getString(R.string.network_error_body))

        val button = dialog.findViewById<Button>(android.R.id.button1)

        assertThat(button.text).isEqualTo(activity.getString(R.string.okay))
        button.performClick()
        assertThat(dialog.isShowing).isFalse()

        verify(next, never()).handleError(error)
    }

    @Test
    fun testUnhandled() {
        // Given
        val error = Error()

        // When
        errorHandler.handleError(error)

        // Then
        assertThat(ShadowDialog.getLatestDialog()).isNull()
        verify(next).handleError(error)
    }
}
