package greenely.greenely.push

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.AndroidInjection
import greenely.greenely.push.data.PushRepo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * @author Anton Holmberg
 */
/**
 * Creates an IntentService.  Invoked by your subclass's constructor.
 */
class PushRegistrationIntentService : JobIntentService() {
    companion object {
        private val JOB_ID = 9094
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, PushRegistrationIntentService::class.java, JOB_ID, work)
        }
    }

    @Inject
    lateinit var repo: PushRepo

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleWork(intent: Intent) {
        val token = FirebaseInstanceId.getInstance().token
        if (token != null) {
            repo.setToken(token).subscribeBy(
                    onError = {
                        Log.d("Error", "Error", it)
                    }
            )
        }
    }
}



