package greenely.greenely.retailinvite.models

data class PromoCodeErrorModel(
        var promocodeError: String
) {
    fun hasErrors(): Boolean {
        return !(promocodeError.isEmpty())
    }

    companion object {
        fun noErrors(): PromoCodeErrorModel =
                PromoCodeErrorModel("")
    }
}