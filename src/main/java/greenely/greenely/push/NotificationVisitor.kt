package greenely.greenely.push

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import greenely.greenely.R
import greenely.greenely.push.data.PushRepo
import greenely.greenely.push.models.*
import greenely.greenely.splash.ui.SplashActivity
import javax.inject.Inject

class NotificationVisitor @Inject constructor(
        private val application: Application,
        private val repo: PushRepo
) : PushMessageVisitor {

    companion object {
        val MESSAGES_CHANNEL_ID = "messages"
        val DATA_CHANNEL_ID = "data"
    }

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(application)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannels(application)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val messagesChannel = NotificationChannel(
                    MESSAGES_CHANNEL_ID,
                    context.getString(R.string.messages),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val dataChannel = NotificationChannel(
                    DATA_CHANNEL_ID,
                    context.getString(R.string.data_updates),
                    NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(messagesChannel)
            notificationManager.createNotificationChannel(dataChannel)
        }
    }

    override fun visit(dataMessage: DataMessage) {

        val notificationIntent = Intent(application, SplashActivity::class.java)

        notificationIntent.putExtra("deep_link", dataMessage.deepLink)

        val contentIntent = PendingIntent.getActivity(
                application,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(application, DATA_CHANNEL_ID)
                .setContentTitle(dataMessage.title)
                .setContentText(dataMessage.body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(contentIntent)
                .setDeleteIntent(createOnDismissedIntent(application, 0))
                .build()

        notificationManager.notify(repo.getNextNotificationId(), notification)
    }

    override fun visit(inboxMessage: InboxMessage) {

        val notificationIntent = Intent(application, SplashActivity::class.java)

        notificationIntent.putExtra("deep_link", MessageType.UNKNOWN)

        val contentIntent = PendingIntent.getActivity(
                application,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(application, MESSAGES_CHANNEL_ID)
                .setContentTitle(inboxMessage.title)
                .setContentText(inboxMessage.body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(contentIntent)
                .setDeleteIntent(createOnDismissedIntent(application, 0))
                .build()

        notificationManager.notify(repo.getNextNotificationId(), notification)
    }

    override fun visit(unknownMessage: UnknownMessage) {
        val notificationIntent = Intent(application, SplashActivity::class.java)
        notificationIntent.putExtra("deep_link", MessageType.UNKNOWN)

        val contentIntent = PendingIntent.getActivity(
                application,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(application, MESSAGES_CHANNEL_ID)
                .setContentTitle(unknownMessage.title)
                .setContentText(unknownMessage.body)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(contentIntent)
                .setDeleteIntent(createOnDismissedIntent(application, 0))
                .build()

        notificationManager.notify(repo.getNextNotificationId(), notification)
    }


    private fun createOnDismissedIntent(context: Context, requestCode: Int): PendingIntent {
        val intent = Intent(context, NotificationDismissedReceiver::class.java)
        return PendingIntent.getBroadcast(context.applicationContext,
                requestCode, intent, 0)
    }
}

