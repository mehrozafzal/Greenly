package greenely.greenely.solaranalysis.ui.householdinfo.events

import android.app.Activity
import android.content.Intent
import greenely.greenely.solaranalysis.data.SendContactInfoIntentService

internal class DoneHandler(
        private val activity: Activity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.Done) {
            val intent = Intent().apply {
                putExtra("analysis", event.analysis)
                putExtra("contactInfo", event.contactInfo)
            }
            SendContactInfoIntentService.enqueueWork(activity, intent)
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

