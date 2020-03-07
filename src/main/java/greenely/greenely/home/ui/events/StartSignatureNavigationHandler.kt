package greenely.greenely.home.ui.events

import android.app.Activity
import android.content.Intent
import greenely.greenely.home.ui.HomeFragment
import greenely.greenely.signature.ui.SignatureActivity

class StartSignatureNavigationHandler(
        private val fragment: HomeFragment,
        private val activity: Activity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event is Event.StartSignatureNavigationEvent) {
            val intent = Intent(fragment.context, SignatureActivity::class.java)
            if (event.requestCode != null) {
                activity.startActivityForResult(intent, event.requestCode)
            } else {
                fragment.context?.startActivity(intent)
            }
        } else {
            next?.handleEvent(event)
        }

    }
}

