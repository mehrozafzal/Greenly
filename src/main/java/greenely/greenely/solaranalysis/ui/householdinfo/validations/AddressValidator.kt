package greenely.greenely.solaranalysis.ui.householdinfo.validations

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.AddressErrors
import greenely.greenely.solaranalysis.models.HouseholdInfo
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

@OpenClassOnDebug
class AddressValidator @Inject constructor(
        private val application: Application
) {
    fun validate(inputModel: HouseholdInfo): AddressErrors {
        return AddressErrors(
                addressError = validateAddress(inputModel),
                postalCodeError = validatePostalCode(inputModel),
                postalRegionError = validatePostalRegion(inputModel)
        )
    }

    private fun validatePostalRegion(inputModel: HouseholdInfo): String =
            inputModel.postalRegion.get().let {
                if (it.isNullOrEmpty() || it.isBlank()) {
                    application.getString(R.string.can_not_be_empty)
                } else ""
            }

    private fun validatePostalCode(inputModel: HouseholdInfo): String =
            inputModel.postalCode.get().let {
                when {
                    it.isNullOrEmpty() || it.isBlank() -> {
                        application.getString(R.string.can_not_be_empty)
                    }
                    it.unformatPostalCode().any { !it.isDigit() } -> {
                        application.getString(R.string.can_only_contain_digits)
                    }
                    it.unformatPostalCode().length != 5 -> {
                        application.getString(R.string.does_not_contain_full_digits)
                    }
                    else -> ""
                }
            }

    private fun validateAddress(inputModel: HouseholdInfo): String =
            if (inputModel.address.get().isNullOrEmpty() || inputModel.address.get().isBlank()) {
                application.getString(R.string.can_not_be_empty)
            } else ""

}