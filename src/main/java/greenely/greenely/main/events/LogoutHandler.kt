package greenely.greenely.main.events

import android.content.Intent
import greenely.greenely.login.ui.LoginActivity
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.splash.ui.SplashActivity
import greenely.greenely.tracking.Tracker

internal class LogoutHandler(
        private val activity: MainActivity,
        private val tracker: Tracker,
        var next: EventHandler? = null,
        private val intent: Intent = Intent(activity, SplashActivity::class.java)
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.Logout) {
            activity.startActivity(intent)
            tracker.reset()
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

