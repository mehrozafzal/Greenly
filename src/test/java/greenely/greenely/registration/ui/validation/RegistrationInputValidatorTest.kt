package greenely.greenely.registration.ui.validation

import android.app.Application
import android.util.Patterns
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import greenely.greenely.registration.ui.models.RegistrationInputModel
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.regex.Pattern

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Patterns::class)
class RegistrationInputValidatorTest {
    private val emailPatter = Pattern.compile("anton")

    private lateinit var application: Application
    private lateinit var validator: RegistrationInputValidator

    @Before
    fun setUp() {
        val field = PowerMockito.field(Patterns::class.java, "EMAIL_ADDRESS")
        field.set(Patterns::class.java, emailPatter)

        application = mock {
            on { getString(R.string.email_empty_error_message) } doReturn "Empty email"
            on { getString(R.string.invalid_email_error_message) } doReturn "Invalid email"
            on { getString(R.string.repeat_email_error_message) } doReturn "Email repeat error"
            on { getString(R.string.password_empty_error_message) } doReturn "Password empty"
            on { getString(R.string.user_agreement_error_message) } doReturn "User agreement error"
        }

        validator = RegistrationInputValidator(application)
    }

    @Test
    fun testEmptyEmail() {
        // Given
        val input = RegistrationInputModel(
                email = NonNullObservableField(""),
                password = NonNullObservableField("password")
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result.emailError).isEqualTo("Empty email")
    }

    @Test
    fun testInvalidEmail() {
        // Given
        val input = RegistrationInputModel(
                email = NonNullObservableField("not anton"),
                password = NonNullObservableField("password")
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result.emailError).isEqualTo("Invalid email")
    }


    @Test
    fun testEmptyPassword() {
        // Given
        val input = RegistrationInputModel(
                email = NonNullObservableField("anton"),
                password = NonNullObservableField("")
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result.passwordError).isEqualTo("Password empty")
    }


    @Test
    fun testValid() {
        // Given
        val input = RegistrationInputModel(
                email = NonNullObservableField("anton"),
                password = NonNullObservableField("password")
        )

        // When
        val result = validator.validate(input)

        // Then
        assertThat(result.hasErrors()).isFalse()
    }
}