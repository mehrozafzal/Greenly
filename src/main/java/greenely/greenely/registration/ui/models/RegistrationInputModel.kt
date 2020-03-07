package greenely.greenely.registration.ui.models

import androidx.databinding.Observable
import greenely.greenely.utils.NonNullObservableField

data class RegistrationInputModel(
        val email: NonNullObservableField<String> = NonNullObservableField(""),
        val password: NonNullObservableField<String> = NonNullObservableField("")
) {
    fun setPropertyChangedCallback(propertyChangedCallback: Observable.OnPropertyChangedCallback) {
        email.addOnPropertyChangedCallback(propertyChangedCallback)
        password.addOnPropertyChangedCallback(propertyChangedCallback)
    }
}

