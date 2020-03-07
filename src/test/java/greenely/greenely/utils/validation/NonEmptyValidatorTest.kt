package greenely.greenely.utils.validation

import android.app.Application
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class NonEmptyValidatorTest {
    private lateinit var application: Application
    private lateinit var validator: NonEmptyValidator

    @Before
    fun setUp() {
        application = mock {
            on { getString(R.string.can_not_be_empty) } doReturn "Can not be empty"
        }

        validator = NonEmptyValidator(application)
    }

    @Test
    fun testEmpty() {
        // When
        val error = validator.validate("")

        // Then
        assertThat(error).isEqualTo("Can not be empty")
    }

    @Test
    fun testNotEmpty() {
        // When
        val error = validator.validate("abc")

        // Then
        assertThat(error).isNull()
    }
}