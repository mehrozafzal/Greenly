package greenely.greenely.retailonboarding.ui.events

import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import javax.inject.Inject

class EventHandlerFactory @Inject constructor(
) {
    fun createEventHandler(activity: RetailOnboardingActivity): EventHandler {
        return DoneHandler(activity)
    }
}
