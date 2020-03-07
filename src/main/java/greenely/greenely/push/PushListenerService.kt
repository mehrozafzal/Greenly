package greenely.greenely.push

import android.content.Intent
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.intercom.android.sdk.push.IntercomPushClient

class PushListenerService : FirebaseMessagingService() {

    private val intercomPushClient = IntercomPushClient()

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val message = remoteMessage?.data
        if (intercomPushClient.isIntercomPush(message!!)) {
            intercomPushClient.handlePush(application, message)
        } else {

            val data = remoteMessage.data ?: return

            if (data.containsKey("message") && !data.containsKey("type")) {

                val notificationBundle = Bundle()
                notificationBundle.putString("message", data.get("message").toString())

                if (data.containsKey("deep_link_url"))
                    notificationBundle.putString("deep_link_url", data.get("deep_link_url").toString())


                PushIntentService.enqueueWork(this, Intent().apply {
                    putExtra("data", notificationBundle)
                })
            } else if (data.containsKey("type")) {

                val dataBundle = Bundle()
                dataBundle.putString("body", data.get("body").toString())
                dataBundle.putString("type", data.get("type").toString())
                dataBundle.putString("title", data.get("title").toString())
                dataBundle.putString("message", data.get("message").toString())

                if (data.containsKey("deep_link_url"))
                    dataBundle.putString("deep_link_url", data.get("deep_link_url").toString())

                PushIntentService.enqueueWork(this, Intent().apply {
                    putExtra("data", dataBundle)
                })
            }
        }
    }

    override fun onNewToken(refreshedToken: String?) {
        if (refreshedToken != null) {
            intercomPushClient.sendTokenToIntercom(application, refreshedToken)
        }
    }
}