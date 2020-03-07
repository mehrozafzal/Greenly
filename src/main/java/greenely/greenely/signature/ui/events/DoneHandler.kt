package greenely.greenely.signature.ui.events

import android.app.Activity
import android.content.Intent
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.signature.ui.SignatureDoneActivity

class DoneHandler(
        private val activity: SignatureActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event == Event.Done) {
            activity.trackPoaSigned()
            activity.startActivity(Intent(activity, SignatureDoneActivity::class.java))
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        } else {
            next?.handleEvent(event)
        }
    }
}

