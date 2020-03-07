package greenely.greenely.solaranalysis.boardcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import greenely.greenely.R

abstract class ContactInfoSentBroadcastReceiver : BroadcastReceiver() {
    companion object {
        fun sendBroadcastIntent(context: Context, intent: Intent = Intent()) {
            intent.action = context.getString(R.string.CONTACT_INFO_SENT)
            context.sendBroadcast(intent)
        }
    }

    fun register(context: Context) {
        val filter = IntentFilter(context.getString(R.string.CONTACT_INFO_SENT))
        context.registerReceiver(this, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}

