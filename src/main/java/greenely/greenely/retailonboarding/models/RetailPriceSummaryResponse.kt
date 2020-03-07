package greenely.greenely.retailonboarding.models

import com.squareup.moshi.Json
import greenely.greenely.retailinvite.models.PriceSummaryItem

data class RetailPriceSummaryResponse(
        @field:Json(name = "price_group_id") val priceGroupId: Int?,
        @field:Json(name = "promocode") val promocode: String?,
        @field:Json(name = "discount_in_kr") val discount: Float,
        @field:Json(name = "contract_type") val contractType: String?,
        @field:Json(name = "rebate_enabled") val rebateEnabled: Boolean,
        @field:Json(name = "rebate_months_count") val rebateMonthsCounts: Int?,
        @field:Json(name = "greenely_fee_in_kr_per_month") val monthlyFee: Float,
        @field:Json(name = "additions") val additions: MutableList<PriceSummaryItem>
)
