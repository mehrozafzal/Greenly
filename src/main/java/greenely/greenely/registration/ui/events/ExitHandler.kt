package greenely.greenely.registration.ui.events

import greenely.greenely.registration.ui.RegistrationActivity

class ExitHandler(
        private val activity: RegistrationActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.Exit) {
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

