package greenely.greenely.utils.validation

import android.app.Application
import android.util.Patterns
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.regex.Pattern


@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Patterns::class)
class EmailValidatorTest {
    private lateinit var application: Application
    private lateinit var validator: EmailValidator

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.email_empty_error_message) } doReturn "Email is required"
            on { getString(R.string.invalid_email_error_message) } doReturn "Invalid email"
        }

        validator = EmailValidator(application)

        PowerMockito.mockStatic(Patterns::class.java)
    }

    @Test
    fun testEmpty() {
        // When
        val error = validator.validate("")

        // Then
        assertThat(error).isEqualTo("Email is required")
    }

    @Test
    fun testInvalid() {
        // Given
        val pattern = Pattern.compile("\$a")
        val field = PowerMockito.field(Patterns::class.java, "EMAIL_ADDRESS")
        field.set(Patterns::class.java, pattern)

        // When
        val error = validator.validate("email")

        // Then
        assertThat(error).isEqualTo("Invalid email")
    }

    @Test
    fun testValid() {
        // Given
        val pattern = Pattern.compile(".*")
        val field = PowerMockito.field(Patterns::class.java, "EMAIL_ADDRESS")
        field.set(Patterns::class.java, pattern)

        // When
        val error = validator.validate("email")

        // Then
        assertThat(error).isNull()
    }
}
