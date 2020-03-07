package greenely.greenely.retail.mappers

import android.app.Application
import greenely.greenely.R
import greenely.greenely.retail.models.Invoice
import greenely.greenely.retail.models.InvoiceJson
import javax.inject.Inject

class InvoiceMapper @Inject constructor(
        application: Application
) {
    private val currencyFormat = application.getString(R.string.currency_format)

    fun fromInvoiceJson(json: InvoiceJson): Invoice {

        return Invoice(
                month = json.month,
                state = json.state,
                isPaid = json.isPaid,
                pdfUrl = json.pdfUrl,
                cost = currencyFormat.format(Math.round(json.cost.div(100.0)))
        )
    }
}
