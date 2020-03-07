package greenely.greenely.registration.ui.models

data class RegistrationErrorModel(
        val emailError: String,
        val passwordError: String
) {
    fun hasErrors(): Boolean {
        return !(emailError.isEmpty() &&
                passwordError.isEmpty())
    }

    companion object {
        fun noErrors(): RegistrationErrorModel =
                RegistrationErrorModel("", "")
    }
}

