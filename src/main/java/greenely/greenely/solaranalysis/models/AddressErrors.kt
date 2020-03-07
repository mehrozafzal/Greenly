package greenely.greenely.solaranalysis.models

import greenely.greenely.OpenClassOnDebug

@OpenClassOnDebug
data class AddressErrors(
        val addressError: String = "",
        val postalCodeError: String = "",
        val postalRegionError: String = ""
) {
    fun hasErrors(): Boolean =
            hasAddressError() || hasPostalCodeError() || hasPostalRegionError()

    private fun hasAddressError(): Boolean = !addressError.isEmpty()
    private fun hasPostalCodeError(): Boolean = !postalCodeError.isEmpty()
    private fun hasPostalRegionError(): Boolean = !postalRegionError.isEmpty()
}