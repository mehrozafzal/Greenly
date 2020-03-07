package greenely.greenely.utils.validation

import android.app.Application
import android.telephony.PhoneNumberUtils
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(PhoneNumberUtils::class)
class PhoneNumberValidatorTest {
    private lateinit var application: Application
    private lateinit var validator: PhoneNumberValidator

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.phone_number_required) } doReturn "Phone number is required"
            on { getString(R.string.invalid_phone_number) } doReturn "Invalid phone number"
            on { getString(R.string.phone_number_must_be_10_digits) } doReturn "Phone number needs to contain 10 digits"
        }

        validator = PhoneNumberValidator(application)

        PowerMockito.mockStatic(PhoneNumberUtils::class.java)
    }

    @Test
    fun testEmptyPhoneNumber() {
        // When
        val error = validator.validate("")

        // Then
        assertThat(error).isEqualTo("Phone number is required")
    }

    @Test
    fun testShortPhoneNumber() {
        // Given
        @Suppress("DEPRECATION")
        PowerMockito.`when`(PhoneNumberUtils.formatNumber("00")).thenReturn("Not null")

        // When
        val error = validator.validate("00")

        // Then
        assertThat(error).isEqualTo("Phone number needs to contain 10 digits")
    }

    @Test
    fun testInvalidPhoneNumber() {
        // Given
        @Suppress("DEPRECATION")
        PowerMockito.`when`(PhoneNumberUtils.formatNumber("0005134680")).thenReturn(null)

        // When
        val error = validator.validate("0005134680")

        // Then
        assertThat(error).isEqualTo("Invalid phone number")
    }

    @Test
    fun testValidPhoneNumber() {
        // Given
        @Suppress("DEPRECATION")
        PowerMockito.`when`(PhoneNumberUtils.formatNumber("0705134680")).thenReturn("Not null")

        // When
        val error = validator.validate("0705134680")

        // Then
        assertThat(error).isNull()
    }
}