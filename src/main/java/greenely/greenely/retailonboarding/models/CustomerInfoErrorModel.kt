package greenely.greenely.retailonboarding.models

data class CustomerInfoErrorModel(
        val personalNumberError: String = "",
        val addressError: String = "",
        val postalCodeError: String = "",
        val postalRegionError: String = "",
        val emailError: String = "",
        val phoneNumberError: String = ""
) {
    fun hasErrors(): Boolean =
            hasAddressStepErrors() || hasContactInfoErrors()

    fun hasAddressStepErrors(): Boolean =
            hasAddressError() || hasPostalCodeError() || hasPostalRegionError()
                    || hasPersonalNumberError()

    fun hasContactInfoErrors(): Boolean =
            hasEmailError() || hasPhoneNumberError()

    fun hasPersonalNumberError(): Boolean = !personalNumberError.isEmpty()
    fun hasAddressError(): Boolean = !addressError.isEmpty()
    fun hasPostalCodeError(): Boolean = !postalCodeError.isEmpty()
    fun hasPostalRegionError(): Boolean = !postalRegionError.isEmpty()
    fun hasEmailError(): Boolean = !emailError.isEmpty()
    fun hasPhoneNumberError(): Boolean = !phoneNumberError.isEmpty()
}
