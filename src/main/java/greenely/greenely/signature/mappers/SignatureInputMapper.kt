package greenely.greenely.signature.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.signature.data.models.PrefillDataResponseModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import javax.inject.Inject

@OpenClassOnDebug
class SignatureInputMapper @Inject constructor() {
    fun updateFromPreFillResponse(
            input: SignatureInputModel,
            preFillData: PrefillDataResponseModel
    ) {
        input.firstName.set(preFillData.firstName)
        input.lastName.set(preFillData.lastName)
        input.address.set(preFillData.adress)
        input.postalCode.set(preFillData.postalCode)
        input.postalRegion.set(preFillData.postalRegion)
    }
}

