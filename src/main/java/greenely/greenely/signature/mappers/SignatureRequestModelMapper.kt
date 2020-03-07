package greenely.greenely.signature.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.signature.data.models.SignatureRequestModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.utils.BitmapToBase64Converter
import org.grunkspin.swedishformats.unformatPersonalNumber
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

@OpenClassOnDebug
class SignatureRequestModelMapper @Inject constructor(private val bitmapToBase64Converter: BitmapToBase64Converter) {
    fun fromSignatureInput(input: SignatureInputModel): SignatureRequestModel {
        return SignatureRequestModel(
                firstName = input.firstName.get(),
                lastName = input.lastName.get(),
                personalNumber = input.personalNumber.get().unformatPersonalNumber(),
                adress = input.address.get(),
                postalRegion = input.postalRegion.get(),
                postalCode = input.postalCode.get().unformatPostalCode(),
                phoneNumber = input.phoneNumber.get()
        )
    }
}

