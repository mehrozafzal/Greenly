package greenely.greenely.registration.mappers

import greenely.greenely.registration.data.models.RegistrationRequest
import greenely.greenely.registration.ui.models.RegistrationInputModel
import javax.inject.Inject

class RegistrationRequestMapper @Inject constructor() {
    fun fromRegistrationInput(input: RegistrationInputModel): RegistrationRequest {
        return RegistrationRequest(input.email.get(), input.password.get())
    }
}

