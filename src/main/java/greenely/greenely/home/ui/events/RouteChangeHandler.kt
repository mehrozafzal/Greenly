package greenely.greenely.home.ui.events

import android.app.Activity
import android.content.Intent
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.signature.ui.SignatureActivity
import javax.inject.Inject

class RouteChangeHandler @Inject constructor(
        private val activity: MainActivity
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.RouteTo) {
            activity.routeTo(event.route)
        }
    }
}
