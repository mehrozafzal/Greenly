package greenely.greenely.retail.models

import com.squareup.moshi.Json
import greenely.greenely.retail.data.CustomerState
import greenely.greenely.retailinvite.models.PriceSummaryItem

data class RetailOverViewJson(
        @field:Json(name = "is_retail_customer") val isRetailCustomer: Boolean,
        @field:Json(name = "can_become_retail_customer") val canBecomeRetailCustomer: Boolean,
        @field:Json(name = "greenely_fee_in_kr_per_month") val monthlyFee: Int?,
        @field:Json(name = "has_unpaid_invoices") val hasUnpaidInvoices: Boolean,
        @field:Json(name = "current_month") val currentMonth: CurrentMonthDetailsJson?,
        @field:Json(name = "next_day") val nextDay: HeaderDetailsJson?,
        @field:Json(name = "current_day") val currentDay: HeaderDetailsJson?,
        @field:Json(name = "retail_state") val state: CustomerState,
        @field:Json(name = "retail_start_date") val startDate: String?,
        @field:Json(name = "retail_state_failed_message") val retailStateFailedMessage: String?,
        @field:Json(name = "additions") val addition: MutableList<PriceSummaryItem>,
        @field:Json(name = "referral_discount_in_kr") val discount: Float,
        val invoices: List<InvoiceJson>?

)
