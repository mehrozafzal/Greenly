package greenely.greenely.signature.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.signature.ui.models.SignatureInputModel
import org.grunkspin.swedishformats.unformatPersonalNumber
import javax.inject.Inject

@OpenClassOnDebug
class PreFillDataRequestMapper @Inject constructor() {
    fun fromSignatureInputModel(model: SignatureInputModel): String {
        return model.personalNumber.get().unformatPersonalNumber()
    }
}

