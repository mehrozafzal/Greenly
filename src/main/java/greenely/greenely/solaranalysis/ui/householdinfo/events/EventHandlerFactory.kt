package greenely.greenely.solaranalysis.ui.householdinfo.events

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.databinding.SolarAnalysisActivityBinding
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisErrorHandler
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import javax.inject.Inject

@OpenClassOnDebug
class EventHandlerFactory @Inject constructor(
        private val activity: SolarAnalysisActivity,
        private val errorHandler: SolarAnalysisErrorHandler
) {
    fun createEventHandler(binding: SolarAnalysisActivityBinding): EventHandler {
        val showLoader = ShowLoadingHandler(binding.loader)
        val showError = ShowErrorHandler(errorHandler)
        val doneHandler = DoneHandler(activity)
        val abortHandler = AbortHandler(activity)

        showLoader.next = showError
        showError.next = doneHandler
        doneHandler.next = abortHandler
        return showLoader
    }
}

