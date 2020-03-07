package greenely.greenely.signature.ui.validation

import android.app.Application
import android.graphics.Bitmap
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.signature.ui.models.SignatureInputErrorModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.utils.InvalidPersonalNumberException
import greenely.greenely.utils.validatePersonalNumber
import greenely.greenely.utils.validation.PhoneNumberValidator
import org.grunkspin.swedishformats.unformatPersonalNumber
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

@OpenClassOnDebug
class SignatureInputValidator @Inject constructor(
        private val application: Application,
        private val phoneNumberValidator: PhoneNumberValidator

) {

    private val facilityIdRegex = Regex("\\d{3} \\d{3} \\d{3} \\d{3} \\d{3} \\d{3}")

    fun validateAllSteps(inputModel: SignatureInputModel): SignatureInputErrorModel {
        return SignatureInputErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = validateAddress(inputModel),
                postalCodeError = validatePostalCode(inputModel),
                postalRegionError = validatePostalRegion(inputModel)
        )
    }

    fun validatePersonalNumberStep(inputModel: SignatureInputModel): SignatureInputErrorModel {
        return SignatureInputErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = "",
                postalCodeError = "",
                postalRegionError = ""
        )
    }

    fun validateAddressStep(inputModel: SignatureInputModel): SignatureInputErrorModel {
        return SignatureInputErrorModel(
                personalNumberError = "",
                addressError = validateAddress(inputModel),
                postalCodeError = validatePostalCode(inputModel),
                postalRegionError = validatePostalRegion(inputModel),
                phoneNumberError = phoneNumberValidator.validate(inputModel.phoneNumber.get()) ?: ""
        )
    }

    fun validatePersonalNumberField(inputModel: SignatureInputModel): SignatureInputErrorModel {
        return SignatureInputErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = "",
                postalCodeError = "",
                postalRegionError = ""
        )
    }

    fun validateSigningStep(inputModel: SignatureInputModel): SignatureInputErrorModel {
        return SignatureInputErrorModel(
                personalNumberError = "",
                addressError = "",
                postalCodeError = "",
                postalRegionError = ""
        )
    }

    private fun validatePostalRegion(inputModel: SignatureInputModel): String =
            inputModel.postalRegion.get().let {
                if (it.isNullOrEmpty() || it.isBlank()) {
                    application.getString(R.string.can_not_be_empty)
                } else ""
            }

    private fun validatePostalCode(inputModel: SignatureInputModel): String =
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

    private fun validatePersonalNumber(inputModel: SignatureInputModel): String =
            try {
                inputModel.personalNumber.get().unformatPersonalNumber().validatePersonalNumber()
                ""
            } catch (ex: InvalidPersonalNumberException) {
                when (ex) {
                    is InvalidPersonalNumberException.InvalidLengthException -> {
                        application.getString(R.string.personal_number_not_long_enough)
                    }
                    is InvalidPersonalNumberException.NotAllDigitsException -> {
                        application.getString(R.string.invalid_personal_number)
                    }
                    is InvalidPersonalNumberException.InvalidCheckNumberException -> {
                        application.getString(R.string.personal_number_not_valid)
                    }
                }
            }

    private fun validateAddress(inputModel: SignatureInputModel): String =
            if (inputModel.address.get().isNullOrEmpty() || inputModel.address.get().isBlank()) {
                application.getString(R.string.can_not_be_empty)
            } else ""



}

