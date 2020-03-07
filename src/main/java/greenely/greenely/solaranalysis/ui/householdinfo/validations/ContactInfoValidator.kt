package greenely.greenely.solaranalysis.ui.householdinfo.validations

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.ContactInfoErrors
import greenely.greenely.utils.validation.EmailValidator
import greenely.greenely.utils.validation.PhoneNumberValidator
import javax.inject.Inject

@OpenClassOnDebug
class ContactInfoValidator @Inject constructor(
        private val application: Application,
        private val emailValidator: EmailValidator,
        private val phoneNumberValidator: PhoneNumberValidator
) {
    val errorModel = ContactInfoErrors()

    fun validate(contactInfo: ContactInfo) {
        errorModel.nameError.set(validateName(contactInfo.name.get()))
        errorModel.emailError.set(emailValidator.validate(contactInfo.email.get()))
        errorModel.phoneNumberError.set(phoneNumberValidator.validate(contactInfo.phoneNumber.get()))
    }

    fun validateName(value: String): String? {
        return if (value.isNullOrEmpty() || value.isBlank()) {
            application.getString(R.string.name_required)
        } else {
            null
        }
    }
}
