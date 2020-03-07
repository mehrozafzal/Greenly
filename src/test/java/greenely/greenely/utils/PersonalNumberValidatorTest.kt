package greenely.greenely.utils

import greenely.greenely.utils.InvalidPersonalNumberException.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.Test


class PersonalNumberValidatorTest {
    @Test
    fun testIncorrectLength() {
        assertThatThrownBy { "123".validatePersonalNumber() }
                .isInstanceOf(InvalidLengthException::class.java)
    }

    @Test
    fun testNotAllDigits() {
        assertThatThrownBy { "11111234567a".validatePersonalNumber() }
                .isInstanceOf(NotAllDigitsException::class.java)
    }

    @Test
    fun testInvalidCheckNumber() {
        assertThatThrownBy { "111111113022".validatePersonalNumber() }
                .isInstanceOf(InvalidCheckNumberException::class.java)
    }

    @Test
    fun testValidCheckNumber() {
        assertThatCode { "111111113021".validatePersonalNumber() }
                .doesNotThrowAnyException()
    }
}
