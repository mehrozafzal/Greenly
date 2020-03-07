package greenely.greenely

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Resources
import androidx.multidex.MultiDex
import androidx.appcompat.app.AppCompatDelegate
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.segment.analytics.Analytics
import dagger.android.*
import greenely.greenely.di.DaggerAppInjector
import io.intercom.android.sdk.Intercom
import net.danlew.android.joda.JodaTimeAndroid
import java.util.*
import javax.inject.Inject

/**
 * Application class

 * @author Anton Holmberg
 */
class GreenelyApplication : Application(), HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingAndroidServiceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var receiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

    /** Handle initialization */
    override fun onCreate() {
        super.onCreate()

        DaggerAppInjector.builder()
                .application(this)
                .build()
                .inject(this)

        setUpJodaTime()
        setupSegment()
        if (!BuildConfig.DEBUG) {
            setupFacebook()
        }

        setUpIntercom()

        resources.configuration.setLocale(Locale("sv", "SE"))
    }

    private fun setUpJodaTime() {
        try {
            JodaTimeAndroid.init(this)
        } catch (ex: Resources.NotFoundException) {
            // Robolectric doesn't like joda time android.
        }
    }

    @Suppress("DEPRECATION")
    private fun setupFacebook() {
        try {
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(this)
        } catch (ex: RuntimeException) {
            Log.e(TAG, "Facebook error")
        }

    }

    private fun setupSegment() {
        try {
            val writeKey = if (BuildConfig.DEBUG)
                getString(R.string.debug_write_key)
            else
                getString(R.string.release_write_key)

            val analytics = Analytics.Builder(this, writeKey).build()
            Analytics.setSingletonInstance(analytics)
        } catch (ex: IllegalArgumentException) {
            // Robolectric complains about no intet.
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingAndroidServiceInjector

    private fun setUpIntercom() {
        Intercom.initialize(this, getString(R.string.intercom_api_key), getString(R.string.intercom_app_id))
    }

    companion object {

        private val TAG = GreenelyApplication::class.java.simpleName

        // For vector graphics
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return receiverInjector
    }
}
