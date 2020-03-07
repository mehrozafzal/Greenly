package greenely.greenely.errors.alert

import android.widget.Button
import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.EmptyActivity
import greenely.greenely.R
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog


@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely",
        application = TestApplication::class
)
class AnyExceptionHandlerTest {

    private lateinit var activity: EmptyActivity
    private lateinit var errorHandler: AnyExceptionHandler

    @Before
    fun setUp() {
        DaggerTestComponent.builder().viewModelProviderFactory(mock()).build().inject(
                RuntimeEnvironment.application as TestApplication
        )

        activity = Robolectric.setupActivity(EmptyActivity::class.java)

        errorHandler = AnyExceptionHandler(activity)
    }

    @Test
    fun testHandleError() {
        // Given
        val error = Throwable()

        // When
        errorHandler.handleError(error)

        // Then
        val dialog = ShadowDialog.getLatestDialog()

        val titleText = dialog.findViewById<TextView>(R.id.alertTitle).text
        val bodyText = dialog.findViewById<TextView>(android.R.id.message).text
        val button = dialog.findViewById<Button>(android.R.id.button1)

        assertThat(titleText).isEqualTo(activity.getString(R.string.unexpected_error_title))
        assertThat(bodyText).isEqualTo(activity.getString(R.string.unexpected_error_body))
        assertThat(dialog.isShowing).isTrue()

        button.performClick()

        assertThat(dialog.isShowing).isFalse()
    }
}
