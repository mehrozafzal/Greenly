package greenely.greenely.retail.ui.events

import greenely.greenely.databinding.PriceSummaryFragmentBinding
import javax.inject.Inject

class RetailInfoEventHandlerFactory @Inject constructor(
) {
    fun createEventHandler(binding: PriceSummaryFragmentBinding): EventHandler {
        val showLoader = LoadingHandler(binding.loader)
        val showView = ViewHandler(binding.scroll)

        showView.next = showLoader
        return showView

    }
}
