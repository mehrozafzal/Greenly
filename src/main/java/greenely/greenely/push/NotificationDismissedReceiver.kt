package greenely.greenely.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.android.AndroidInjection
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class NotificationDismissedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tracker: Tracker

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)
        trackEvent("Push Notification Dismissed")
        Log.d("notificationdebug", "notification has been dismissed!")
    }

    private fun trackEvent(label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                "push_notification_dismissed",
                "Push Notification",
                label
        ))
    }
}
