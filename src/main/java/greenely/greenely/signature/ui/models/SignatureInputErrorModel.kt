package greenely.greenely.signature.ui.models

data class SignatureInputErrorModel(
        val personalNumberError: String = "",
        val addressError: String = "",
        val postalCodeError: String = "",
        val postalRegionError: String = "",
        val phoneNumberError: String = ""

) {
    fun hasErrors(): Boolean =
            hasAddressStepErrors() || hasPersonalNumberStepErrors()

    fun hasPersonalNumberStepErrors(): Boolean = hasPersonalNumberError()

    fun hasAddressStepErrors(): Boolean = hasAddressError() || hasPostalCodeError()
            || hasPostalRegionError() || hasPhoneNumberError()

    fun hasPersonalNumberError(): Boolean = !personalNumberError.isEmpty()


    fun hasAddressError(): Boolean = !addressError.isEmpty()
    fun hasPostalCodeError(): Boolean = !postalCodeError.isEmpty()
    fun hasPostalRegionError(): Boolean = !postalRegionError.isEmpty()
    fun hasPhoneNumberError(): Boolean = !phoneNumberError.isEmpty()
}
