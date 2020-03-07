package greenely.greenely.login.ui.events

import greenely.greenely.login.ui.LoginActivity

class ExitHandler(
        private val activity: LoginActivity,
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

