package greenely.greenely.solaranalysis.data

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import androidx.core.app.JobIntentService
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.segment.analytics.Properties
import dagger.android.AndroidInjection
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.boardcasts.ContactInfoSentBroadcastReceiver
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.tracking.Tracker
import javax.inject.Inject


@Suppress("DEPRECATION")
@OpenClassOnDebug
class SendContactInfoIntentService : JobIntentService() {
    companion object {
        private val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, SendContactInfoIntentService::class.java, JOB_ID, work)
        }
    }

    @Inject
    internal lateinit var repo: SolarAnalysisRepo

    @Inject
    internal lateinit var tracker: Tracker

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onHandleWork(intent: Intent) {
        val (analysis: Analysis?, contactInfo: ContactInfo?, any: Any?) =
                extractDataFromIntent(intent)

        if (analysis != null && contactInfo != null) {
            try {
                repo.sendContactInformation(analysis, contactInfo).blockingAwait()
                ContactInfoSentBroadcastReceiver.sendBroadcastIntent(application)
                showSuccessToast()
            } catch (ex: Throwable) {
                showErrorToast()
            }
        } else {
            showErrorToast()
        }
    }

    private fun extractDataFromIntent(intent: Intent): Triple<Analysis?, ContactInfo?, Any?> {
        val analysis: Analysis? = intent.getParcelableExtra("analysis")
        val contactInfo: ContactInfo? = intent.getParcelableExtra("contactInfo")
        return Triple(analysis, contactInfo, null)
    }

    private fun showErrorToast() {
        Handler(Looper.getMainLooper()).post {
            val toast = Toast.makeText(
                    application,
                    R.string.could_not_send_contact_information,
                    Toast.LENGTH_LONG
            ).apply {
                setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                view?.background?.setColorFilter(resources.getColor(R.color.background_floating_material_dark), PorterDuff.Mode.SRC_IN)
                val text: TextView? = view?.findViewById(android.R.id.message)
                text?.setTextColor(resources.getColor(R.color.white))
            }

            val toastCountDown = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    toast.show()
                }

                override fun onFinish() {
                    toast.cancel()
                }
            }

            toast.show()
            toastCountDown.start()
        }
    }

    private fun showSuccessToast() {
        Handler(Looper.getMainLooper()).post {
            val toast = Toast.makeText(
                    application,
                    R.string.contact_information_sent,
                    Toast.LENGTH_LONG
            ).apply {
                setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
                view?.background?.setColorFilter(resources.getColor(R.color.background_floating_material_dark), PorterDuff.Mode.SRC_IN)
                val text: TextView? = view?.findViewById(android.R.id.message)
                text?.setTextColor(resources.getColor(R.color.white))
            }

            val toastCountDown = object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    toast.show()
                }

                override fun onFinish() {
                    toast.cancel()
                }
            }

            toast.show()
            toastCountDown.start()
        }
    }


    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }


    internal class SentConfirmationEvent : Tracker.Event {
        override val name: String
            get() = "Solar Analysis Send Confirmation"
        override val properties: Properties?
            get() = null

    }
}

