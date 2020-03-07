package greenely.greenely.signature.ui.events

import greenely.greenely.signature.ui.SignatureActivity

class ExitHandler constructor(
        private val activity: SignatureActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event == Event.Exit) {
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

