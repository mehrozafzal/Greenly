package greenely.greenely.signature.mappers

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import android.graphics.Bitmap
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.signature.data.models.SignatureRequestModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.utils.BitmapToBase64Converter
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SignatureRequestModelMapperTest {
    private lateinit var bitmapToBase64: BitmapToBase64Converter
    private lateinit var mapper: SignatureRequestModelMapper

    @Mock
    private lateinit var bitmapMock: Bitmap

    @Test
    fun testFromSignatureInput() {
        // Given
        bitmapToBase64 = mock {
            on { toBase64(any()) } doReturn "I am a bitmap in base64"
        }

        MockitoAnnotations.initMocks(this)

        mapper = SignatureRequestModelMapper(bitmapToBase64)

        val signatureInput = SignatureInputModel(
                personalNumber = NonNullObservableField("123456-7890"),
                firstName = NonNullObservableField("Anton"),
                lastName = NonNullObservableField("Holmberg"),
                address = NonNullObservableField("En gata 123"),
                postalCode = NonNullObservableField("123 45"),
                postalRegion = NonNullObservableField("A postal region"),
                phoneNumber = NonNullObservableField("07425116")
        )

        // When
        val result = mapper.fromSignatureInput(signatureInput)

        // Then
        assertThat(result).isEqualTo(SignatureRequestModel(
                personalNumber = "1234567890",
                firstName = "Anton",
                lastName = "Holmberg",
                adress = "En gata 123",
                postalCode = "12345",
                postalRegion = "A postal region",
                phoneNumber = "07425116"
        ))
    }

    @Test
    fun testFromSignatureInputWithFacilityId() {
        // Given
        bitmapToBase64 = mock {
            on { toBase64(any()) } doReturn "I am a bitmap in base64"
        }

        MockitoAnnotations.initMocks(this)

        mapper = SignatureRequestModelMapper(bitmapToBase64)

        val signatureInput = SignatureInputModel(
                personalNumber = NonNullObservableField("123456-7890"),
                firstName = NonNullObservableField("Anton"),
                lastName = NonNullObservableField("Holmberg"),
                address = NonNullObservableField("En gata 123"),
                postalRegion = NonNullObservableField("A postal region"),
                postalCode = NonNullObservableField("123 45"),
                phoneNumber = NonNullObservableField("07425116")
        )

        // When
        val result = mapper.fromSignatureInput(signatureInput)

        // Then
        assertThat(result).isEqualTo(SignatureRequestModel(
                personalNumber = "1234567890",
                firstName = "Anton",
                lastName = "Holmberg",
                adress = "En gata 123",
                postalRegion = "A postal region",
                postalCode = "12345",
                phoneNumber = "07425116"
        ))
    }
}