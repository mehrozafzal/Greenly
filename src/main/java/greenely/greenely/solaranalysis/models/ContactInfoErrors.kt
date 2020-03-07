package greenely.greenely.solaranalysis.models

import androidx.databinding.ObservableField
import greenely.greenely.OpenClassOnDebug

@OpenClassOnDebug
data class ContactInfoErrors(
        val nameError: ObservableField<String> = ObservableField(""),
        val emailError: ObservableField<String> = ObservableField(""),
        val phoneNumberError: ObservableField<String> = ObservableField("")
) {
    fun hasErrors(): Boolean {
        return !nameError.get().isNullOrEmpty() ||
                !emailError.get().isNullOrEmpty() ||
                !phoneNumberError.get().isNullOrEmpty()
    }
}


