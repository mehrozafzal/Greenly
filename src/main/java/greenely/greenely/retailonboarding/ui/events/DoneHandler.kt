package greenely.greenely.retailonboarding.ui.events

import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity

class DoneHandler(
        private val activity: RetailOnboardingActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event == Event.Done) {
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}