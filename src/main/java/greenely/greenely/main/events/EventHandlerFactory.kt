package greenely.greenely.main.events

import greenely.greenely.main.ui.MainActivity
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class EventHandlerFactory @Inject constructor(
        private val activity: MainActivity,
        private val tracker: Tracker
) {
    fun create(): EventHandler {
        return LogoutHandler(activity, tracker)
    }
}

