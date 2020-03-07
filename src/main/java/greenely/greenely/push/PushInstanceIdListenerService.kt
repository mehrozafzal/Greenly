package greenely.greenely.push

import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceIdService

class PushInstanceIdListenerService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        PushRegistrationIntentService.enqueueWork(this, Intent())
    }
}

