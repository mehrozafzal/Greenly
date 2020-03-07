package greenely.greenely.retailinvite.models

import com.squareup.moshi.Json
import greenely.greenely.R

class PriceSummaryItem(
        @field:Json(name = "name") val key: String,
        @field:Json(name = "price_string") val value: String,
        var stylePref: StylePref = StylePref()
)


class StylePref(
        var highLightValue: Boolean = false,
        var isStikeThrough: Boolean = false,
        var highLightColorId: Int = R.color.grey3,
        var isBold: Boolean = false
)

{
    fun isStyleApplicable()= highLightValue || isStikeThrough || isBold
}