package greenely.greenely.push

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import dagger.android.AndroidInjection
import greenely.greenely.push.data.PushRepo
import greenely.greenely.push.mappers.PushMessageMapper
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class PushIntentService : JobIntentService() {
    companion object {
        private val JOB_ID = 5432
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, PushIntentService::class.java, JOB_ID, work)
        }
    }

    @Inject
    lateinit var messageMapper: PushMessageMapper

    @Inject
    lateinit var repo: PushRepo

    @Inject
    lateinit var notificationVisitor: NotificationVisitor

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        val data = intent.getBundleExtra("data")
        repo.isAuthenticated().subscribeBy(onSuccess = {
            if (it) messageMapper.fromBundle(data)?.accept(notificationVisitor)
        })
    }
}

