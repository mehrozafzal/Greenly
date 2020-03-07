package greenely.greenely.signature.mappers

import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PreFillDataRequestMapperTest {
    @Test
    fun testFromSignatureInputModel() {
        // Given
        val mapper = PreFillDataRequestMapper()
        val input = SignatureInputModel(personalNumber = NonNullObservableField("1234"))

        // when
        val result = mapper.fromSignatureInputModel(input)

        // Then
        assertThat(result).isEqualTo("1234")
    }
}