package greenely.greenely.retail.data

import greenely.greenely.retail.mappers.CostMapper
import greenely.greenely.retail.mappers.InvoiceMapper
import greenely.greenely.retail.mappers.MonthlyFeeMapper
import greenely.greenely.retail.models.HeaderDetailsConsumption
import greenely.greenely.retail.models.HeaderDetailsPricing
import greenely.greenely.retail.models.RetailOverViewJson
import greenely.greenely.retail.models.RetailOverview
import javax.inject.Inject

abstract class RetailResponseProcessor {

    open var next: RetailResponseProcessor? = null

    abstract fun processResponse(response: RetailOverViewJson): RetailOverViewJson
}

/**
 * Process out of capacity
 */
class ProcessCanNotBecomeRetailCustomer(
        private val retailOverviewModelBuilder: RetailOverview.Builder,
        private val feeMapper: MonthlyFeeMapper
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {
        if (!response.isRetailCustomer && !response.canBecomeRetailCustomer) {
            retailOverviewModelBuilder.currentDay = null
            retailOverviewModelBuilder.nextDay = null
            retailOverviewModelBuilder.currentMonth = null
            retailOverviewModelBuilder.invoices = null
            retailOverviewModelBuilder.showInvoiceNotAvailable = false
            retailOverviewModelBuilder.onBoardingAllowed = false
            retailOverviewModelBuilder.state = CustomerState.EMPTY
            response.monthlyFee?.let {
                retailOverviewModelBuilder.monthlyFee = feeMapper.fromMonthlyFeeJson(response.monthlyFee)
            }
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices

        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount


        return response.let { next?.processResponse(it) ?: it }
    }

}

/**
 * Process retail onboarding
 */

class ProcessCanBecomeRetailCustomer(
        private val retailOverviewModelBuilder: RetailOverview.Builder,
        private val feeMapper: MonthlyFeeMapper
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {
        if (!response.isRetailCustomer && response.canBecomeRetailCustomer) {
            retailOverviewModelBuilder.currentDay = null
            retailOverviewModelBuilder.nextDay = null
            retailOverviewModelBuilder.currentMonth = null
            retailOverviewModelBuilder.invoices = null
            retailOverviewModelBuilder.showInvoiceNotAvailable = false
            retailOverviewModelBuilder.onBoardingAllowed = true
            retailOverviewModelBuilder.state = CustomerState.EMPTY
            response.monthlyFee?.let {
                retailOverviewModelBuilder.monthlyFee = feeMapper.fromMonthlyFeeJson(response.monthlyFee)
            }
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices
        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount

        return response.let { next?.processResponse(it) ?: it }
    }

}

/**
 * Process onboarding is done
 */

class ProcessWaiting(
        private val retailOverviewModelBuilder: RetailOverview.Builder
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {
        if (response.isRetailCustomer && response.state == CustomerState.WAITING) {
            retailOverviewModelBuilder.currentDay = null
            retailOverviewModelBuilder.nextDay = null
            retailOverviewModelBuilder.currentMonth = null
            retailOverviewModelBuilder.invoices = null
            retailOverviewModelBuilder.showInvoiceNotAvailable = false
            retailOverviewModelBuilder.onBoardingAllowed = false
            retailOverviewModelBuilder.state = CustomerState.WAITING
            retailOverviewModelBuilder.monthlyFee = null
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices
        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount

        return response.let { next?.processResponse(it) ?: it }
    }

}

/**
 * Process completed
 */

class ProcessCompleted(
        private val retailOverviewModelBuilder: RetailOverview.Builder,
        private val costMapper: CostMapper
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {

        if (response.isRetailCustomer && response.state == CustomerState.COMPLETED) {
            retailOverviewModelBuilder.currentDay = HeaderDetailsPricing(
                    title = response.currentDay?.title ?: "",
                    subTitle = response.currentDay?.subTitle ?: "",
                    points = response.currentDay?.points ?: listOf(),
                    value = costMapper.formatCost(response.currentDay?.cost, 2)
            )
            retailOverviewModelBuilder.nextDay = HeaderDetailsPricing(
                    title = response.nextDay?.title ?: "",
                    subTitle = response.nextDay?.subTitle ?: "",
                    points = response.nextDay?.points ?: listOf(),
                    value = costMapper.formatCost(response.nextDay?.cost, 2)
            )
            retailOverviewModelBuilder.currentMonth = HeaderDetailsConsumption(
                    title = response.currentMonth?.title ?: "",
                    subTitle = response.currentMonth?.subTitle ?: "",
                    points = response.currentMonth?.points ?: listOf(),
                    value = response.startDate ?: ""
            )
            retailOverviewModelBuilder.showInvoiceNotAvailable = true
            retailOverviewModelBuilder.state = CustomerState.COMPLETED
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices

        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount

        return response.let { next?.processResponse(it) ?: it }
    }
}

/**
 * Process retail state failed
 */

class ProcessError(
        private val retailOverviewModelBuilder: RetailOverview.Builder
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {
        if (response.isRetailCustomer && response.state == CustomerState.FAILED) {
            retailOverviewModelBuilder.currentDay = null
            retailOverviewModelBuilder.nextDay = null
            retailOverviewModelBuilder.currentMonth = null
            retailOverviewModelBuilder.invoices = null
            retailOverviewModelBuilder.showInvoiceNotAvailable = false
            retailOverviewModelBuilder.onBoardingAllowed = false
            retailOverviewModelBuilder.state = CustomerState.FAILED
            retailOverviewModelBuilder.monthlyFee = null
            retailOverviewModelBuilder.retailFailedMessage = response.retailStateFailedMessage
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices
        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount


        return response.let { next?.processResponse(it) ?: it }
    }

}

/**
 * Process retail customer
 */

class ProcessIsRetailCustomer(
        private val retailOverviewModelBuilder: RetailOverview.Builder,
        private val invoiceMapper: InvoiceMapper,
        private val costMapper: CostMapper
) : RetailResponseProcessor() {

    override fun processResponse(response: RetailOverViewJson): RetailOverViewJson {
        if (response.isRetailCustomer && response.state == CustomerState.OPERATIONAL) {
            retailOverviewModelBuilder.currentDay = HeaderDetailsPricing(
                    title = response.currentDay?.title ?: "",
                    subTitle = response.currentDay?.subTitle ?: "",
                    points = response.currentDay?.points ?: listOf(),
                    value = costMapper.formatCost(response.currentDay?.cost, 2)
            )
            retailOverviewModelBuilder.nextDay = HeaderDetailsPricing(
                    title = response.nextDay?.title ?: "",
                    subTitle = response.nextDay?.subTitle ?: "",
                    points = response.nextDay?.points ?: listOf(),
                    value = costMapper.formatCost(response.nextDay?.cost, 2)
            )
            retailOverviewModelBuilder.currentMonth = HeaderDetailsConsumption(
                    title = response.currentMonth?.title ?: "",
                    subTitle = response.currentMonth?.subTitle ?: "",
                    points = response.currentMonth?.points ?: listOf(),
                    value = costMapper.formatCost(response.currentMonth?.cost, 0)
            )
            retailOverviewModelBuilder.state = CustomerState.OPERATIONAL

            response.invoices?.let {
                if (response.invoices.isEmpty()) {
                    retailOverviewModelBuilder.showInvoiceNotAvailable = true
                } else retailOverviewModelBuilder.invoices = response.invoices.map {
                    invoiceMapper.fromInvoiceJson(it)
                }
            }
            retailOverviewModelBuilder.hasUnpaidInvoices = response.hasUnpaidInvoices
        }
        retailOverviewModelBuilder.additions=response.addition
        retailOverviewModelBuilder.discount=response.discount


        return response.let { next?.processResponse(it) ?: it }
    }
}

class RetailOverviewFactory @Inject constructor(
        private val invoiceMapper: InvoiceMapper,
        private val feeMapper: MonthlyFeeMapper,
        private val costMapper: CostMapper
) {

    fun createRetailOverviewModel(
            retailOverViewJson: RetailOverViewJson
    ): RetailOverview {
        val builder = RetailOverview.Builder()

        val processCanNotBecomeRetailCustomer = ProcessCanNotBecomeRetailCustomer(builder, feeMapper)
        val processCanBecomeRetailCustomer = ProcessCanBecomeRetailCustomer(builder, feeMapper)
        val processWaiting = ProcessWaiting(builder)
        val processCompleted = ProcessCompleted(builder, costMapper)
        val processError = ProcessError(builder)
        val isRetailCustomer = ProcessIsRetailCustomer(builder, invoiceMapper, costMapper)

        processCanNotBecomeRetailCustomer.next = processCanBecomeRetailCustomer
        processCanBecomeRetailCustomer.next = processWaiting
        processWaiting.next = processCompleted
        processCompleted.next = processError
        processError.next = isRetailCustomer

        processCanNotBecomeRetailCustomer.processResponse(retailOverViewJson)

        return builder.build()
    }
}


