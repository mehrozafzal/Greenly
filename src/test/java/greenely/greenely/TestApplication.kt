package greenely.greenely

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Service
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import java.util.*
import javax.inject.Inject

/**
 * The application used for testing.
 *
 * Overriding the base dependency injection.
 */
@SuppressLint("Registered")
class TestApplication : Application(), HasActivityInjector, HasServiceInjector {

    /**
     * Set up.
     */
    override fun onCreate() {
        super.onCreate()
        resources.configuration.setLocale(Locale("sv", "SE"))
    }

    /**
     * The injector. Injected from a test component.
     */
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    /**
     * Return the injector.
     */
    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector
}