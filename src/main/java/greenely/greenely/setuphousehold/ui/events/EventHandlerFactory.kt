package greenely.greenely.setuphousehold.ui.events

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.databinding.SetupHouseholdActivityBinding
import greenely.greenely.setuphousehold.ui.SetupHouseholdActivity
import javax.inject.Inject

@OpenClassOnDebug
class EventHandlerFactory @Inject constructor(
        private val activity: SetupHouseholdActivity
) {
    fun createEventHandler(binding: SetupHouseholdActivityBinding): EventHandler {
        val showLoader = ShowLoadingHandler(binding.loader)
        val abortHandler = AbortHandler(activity)

        showLoader.next = abortHandler
        return showLoader
    }
}
