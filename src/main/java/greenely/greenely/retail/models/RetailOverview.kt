package greenely.greenely.retail.models

import greenely.greenely.retail.data.CustomerState
import greenely.greenely.retailinvite.models.PriceSummaryItem

data class RetailOverview(
        val currentMonth: HeaderDetailsConsumption?,
        val nextDay: HeaderDetailsPricing?,
        val currentDay: HeaderDetailsPricing?,
        val hasUnpaidInvoices: Boolean,
        val invoices: List<Invoice>?,
        val showInvoiceNotAvailableText: Boolean,
        val monthlyFee: String?,
        val onBoardingAllowed: Boolean,
        val state: CustomerState?,
        val retailFailedMessage: String?,
        val additions: MutableList<PriceSummaryItem> = mutableListOf(),
        val discount: Float

) {

    class Builder(
            var currentMonth: HeaderDetailsConsumption? = null,
            var nextDay: HeaderDetailsPricing? = null,
            var currentDay: HeaderDetailsPricing? = null,
            var hasUnpaidInvoices: Boolean = false,
            var invoices: List<Invoice>? = null,
            var showInvoiceNotAvailable: Boolean = false,
            var monthlyFee: String? = null,
            var onBoardingAllowed: Boolean = false,
            var state: CustomerState? = null,
            var retailFailedMessage: String? = null,
            var additions: MutableList<PriceSummaryItem> = mutableListOf(),
            var discount: Float=0f
    ) {
        fun build(): RetailOverview = RetailOverview(
                currentMonth,
                nextDay,
                currentDay,
                hasUnpaidInvoices,
                invoices,
                showInvoiceNotAvailable,
                monthlyFee,
                onBoardingAllowed,
                state,
                retailFailedMessage,
                additions,
                discount
        )
    }
}

