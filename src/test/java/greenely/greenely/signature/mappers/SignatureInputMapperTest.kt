package greenely.greenely.signature.mappers

import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SignatureInputMapperTest {
    @Test
    fun testUpdateFromPreFillResponse() {
        // Given
        val mapper = SignatureInputMapper()
        val input = SignatureInputModel()
        val preFillData = PrefillDataResponseModel(
                "Anton",
                "Holmberg",
                "En gata 123",
                "A postal region",
                "12345"
        )

        // When
        mapper.updateFromPreFillResponse(input, preFillData)

        // Then
        assertThat(input.firstName.get()).isEqualTo("Anton")
        assertThat(input.lastName.get()).isEqualTo("Holmberg")
        assertThat(input.address.get()).isEqualTo("En gata 123")
        assertThat(input.postalRegion.get()).isEqualTo("A postal region")
        assertThat(input.postalCode.get()).isEqualTo("12345")
    }
}