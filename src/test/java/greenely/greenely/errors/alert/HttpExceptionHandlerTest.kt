package greenely.greenely.errors.alert

import android.widget.Button
import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.EmptyActivity
import greenely.greenely.R
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import greenely.greenely.errors.ErrorHandler
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import retrofit2.HttpException
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely",
        application = TestApplication::class
)
class HttpExceptionHandlerTest {

    private lateinit var activity: EmptyActivity
    private lateinit var errorHandler: HttpExceptionHandler
    private lateinit var next: ErrorHandler

    @Before
    fun setUp() {
        next = mock()

        DaggerTestComponent.builder().viewModelProviderFactory(mock()).build().inject(
                RuntimeEnvironment.application as TestApplication
        )

        activity = Robolectric.setupActivity(EmptyActivity::class.java)

        errorHandler = HttpExceptionHandler(activity)
        errorHandler.next = next
    }

    @Test
    fun testHandleError() {
        // Given
        val title = "Title"
        val description = "Body"
        val error = HttpException(createErrorResponse(title, description))

        // When
        errorHandler.handleError(error)

        // Then
        val dialog = ShadowDialog.getLatestDialog()

        val titleText = dialog.findViewById<TextView>(R.id.alertTitle).text
        val bodyText = dialog.findViewById<TextView>(android.R.id.message).text
        val button = dialog.findViewById<Button>(android.R.id.button1)

        assertThat(titleText).isEqualTo(title)
        assertThat(bodyText).isEqualTo(description)

        button.performClick()
        assertThat(button.isShown).isFalse()
    }

    @Test
    fun testWithServerError() {
        // Given
        val error = HttpException(
                Response.error<Any>(500, ResponseBody.create(
                        MediaType.parse("application/json"),
                        ""
                ))
        )

        // When
        errorHandler.handleError(error)

        // Then
        val dialog = ShadowDialog.getLatestDialog()

        val titleText = dialog.findViewById<TextView>(R.id.alertTitle).text
        val bodyText = dialog.findViewById<TextView>(android.R.id.message).text
        val button = dialog.findViewById<Button>(android.R.id.button1)

        assertThat(titleText).isEqualTo(activity.getString(R.string.unexpected_error_title))
        assertThat(bodyText).isEqualTo(activity.getString(R.string.unexpected_error_body))

        button.performClick()
        assertThat(button.isShown).isFalse()
    }

    @Test
    fun testUnhandled() {
        // Given
        val error = Error()

        // When
        errorHandler.handleError(error)

        // Then
        val dialog = ShadowDialog.getLatestDialog()

        assertThat(dialog).isNull()
        verify(next).handleError(error)
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

