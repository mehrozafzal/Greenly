package greenely.greenely.retail.ui.events

import greenely.greenely.databinding.RetailInvoicesBinding
import javax.inject.Inject

class RetailInvoiceEventHandlerFactory @Inject constructor(
) {
    fun createEventHandler(binding: RetailInvoicesBinding): EventHandler {
        val showLoader = LoadingHandler(binding.loader)
        val showView = ViewHandler(binding.scroll)

        showView.next = showLoader
        return showView

    }
}
