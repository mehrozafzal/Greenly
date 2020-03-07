package greenely.greenely.retailonboarding.ui.validations

import android.app.Application
import greenely.greenely.R
import greenely.greenely.retailonboarding.models.CustomerInfoErrorModel
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import greenely.greenely.utils.InvalidPersonalNumberException
import greenely.greenely.utils.validatePersonalNumber
import greenely.greenely.utils.validation.EmailValidator
import greenely.greenely.utils.validation.PhoneNumberValidator
import org.grunkspin.swedishformats.unformatPersonalNumber
import org.grunkspin.swedishformats.unformatPostalCode
import javax.inject.Inject

class CustomerInfoValidator @Inject constructor(
        private val application: Application,
        private val emailValidator: EmailValidator,
        private val phoneNumberValidator: PhoneNumberValidator
) {

    fun validatePersonalNumberStep(inputModel: CustomerInfoModel): CustomerInfoErrorModel {
        return CustomerInfoErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = "",
                postalCodeError = "",
                postalRegionError = "",
                emailError = "",
                phoneNumberError = ""
        )
    }

    fun validateAddressStep(inputModel: CustomerInfoModel): CustomerInfoErrorModel {
        return CustomerInfoErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = validateAddress(inputModel),
                postalCodeError = validatePostalCode(inputModel),
                postalRegionError = validatePostalRegion(inputModel),
                emailError = "",
                phoneNumberError = ""
        )
    }

    fun validateContactInformationStep(inputModel: CustomerInfoModel): CustomerInfoErrorModel {
        return CustomerInfoErrorModel(
                personalNumberError = "",
                addressError = "",
                postalCodeError = "",
                postalRegionError = "",
                emailError = emailValidator.validate(inputModel.email.get()) ?: "",
                phoneNumberError = phoneNumberValidator.validate(inputModel.phoneNumber.get()) ?: ""
        )
    }

    fun validatePersonalNumberField(inputModel: CustomerInfoModel): CustomerInfoErrorModel {
        return CustomerInfoErrorModel(
                personalNumberError = validatePersonalNumber(inputModel),
                addressError = "",
                postalCodeError = "",
                postalRegionError = "",
                emailError = "",
                phoneNumberError = ""
        )
    }

    private fun validatePostalRegion(inputModel: CustomerInfoModel): String =
            inputModel.postalRegion.get().let {
                if (it.isNullOrEmpty() || it.isBlank()) {
                    application.getString(R.string.can_not_be_empty)
                } else ""
            }

    private fun validatePostalCode(inputModel: CustomerInfoModel): String =
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

    private fun validateAddress(inputModel: CustomerInfoModel): String =
            if (inputModel.address.get().isNullOrEmpty() || inputModel.address.get().isBlank()) {
                application.getString(R.string.can_not_be_empty)
            } else ""

    private fun validatePersonalNumber(inputModel: CustomerInfoModel): String =
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
}

