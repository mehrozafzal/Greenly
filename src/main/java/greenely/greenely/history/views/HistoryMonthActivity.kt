package greenely.greenely.history.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.R
import greenely.greenely.history.HistoryFragment
import greenely.greenely.history.HistoryResolution
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Activity for displaying month view of history.
 */
class HistoryMonthActivity : AppCompatActivity(), HasSupportFragmentInjector {

    companion object {
        private val dateKey = "MONTH_DATE"

        /**
         * Create an intent to start the activity.
         *
         * @param context The context starting the activity.
         * @param month The date of the month that we want to show.
         */
        fun createStartIntent(context: Context, month: DateTime?) =
                Intent(context, HistoryMonthActivity::class.java).apply {
                    month?.let { putExtra(dateKey, month.millis) }
                }
    }

    @Inject
    internal lateinit var fragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

        val date = getDate()
        if (date != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, HistoryFragment.create(HistoryResolution.Month(date)))
                    .commit()
        } else {
            finish()
        }
    }

    @VisibleForTesting
    internal fun getDate(): DateTime? {
        val timestamp = intent?.getLongExtra(dateKey, -1L)
        if (timestamp != null && timestamp != -1L) {
            return DateTime(timestamp)
        } else {
            return null
        }
    }

    /**
     * Get the injector to use for injecting child fragments.
     */
    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = fragmentInjector
}