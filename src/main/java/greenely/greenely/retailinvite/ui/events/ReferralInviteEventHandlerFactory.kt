package greenely.greenely.retailinvite.ui.events

import greenely.greenely.databinding.RetailInviteFragmentBinding
import greenely.greenely.retail.ui.events.EventHandler
import greenely.greenely.retail.ui.events.LoadingHandler
import greenely.greenely.retail.ui.events.ViewHandler
import javax.inject.Inject

class ReferralInviteEventHandlerFactory @Inject constructor(
) {
    fun createEventHandler(binding: RetailInviteFragmentBinding): EventHandler {
        val showLoader = LoadingHandler(binding.loader)
        val showView = ViewHandler(binding.scroll)

        showView.next = showLoader
        return showView

    }
}