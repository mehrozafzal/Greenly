package greenely.greenely.retailinvite.util

import android.app.Application
import greenely.greenely.R
import greenely.greenely.retailinvite.models.PromoCodeErrorModel
import greenely.greenely.retailinvite.models.PromocodeData
import javax.inject.Inject

class PromocodeInputValidator @Inject constructor(private val application: Application) {
    fun validate(input: PromocodeData): PromoCodeErrorModel {
        return PromoCodeErrorModel(
                validatePromocode(input.promocode) ?: ""
        )
    }

    private fun validatePromocode(promocode: String): String? =
            if (promocode.isEmpty() || promocode.isBlank()) {
                application.getString(R.string.promocode_empty_error_message)
            }  else {
                null
            }

}