package greenely.greenely.retail.ui.events

import greenely.greenely.databinding.RetailFragmentBinding
import javax.inject.Inject

class EventHandlerFactory @Inject constructor(
) {
    fun createEventHandler(binding: RetailFragmentBinding): EventHandler {
        val showLoader = LoadingHandler(binding.loader)
        val showView = ViewHandler(binding.container)

        showView.next = showLoader
        return showView

    }
}
