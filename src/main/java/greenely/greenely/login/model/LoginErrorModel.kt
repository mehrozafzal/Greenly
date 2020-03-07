package greenely.greenely.login.model

data class LoginErrorModel(val emailError: String, val passwordError: String) {

    fun hasErrors(): Boolean {
        return !(emailError.isEmpty() && passwordError.isEmpty())
    }

    companion object {
        fun noErrors(): LoginErrorModel = LoginErrorModel("", "")
    }
}