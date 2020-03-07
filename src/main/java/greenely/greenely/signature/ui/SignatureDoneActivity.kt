package greenely.greenely.signature.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.segment.analytics.Properties
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import greenely.greenely.R
import greenely.greenely.signature.OnboardCompleteListener
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class SignatureDoneActivity : AppCompatActivity(),HasSupportFragmentInjector,OnboardCompleteListener {

    @Inject
    internal lateinit var tracker: Tracker


    override fun onboardCompleted() {
        tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_done),getString(R.string.poa_category),"PoA Process"))
    }

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = dispatchingFragmentInjector


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signature_done_activity)

//        findViewById<Button>(R.id.doneButton).setOnClickListener {
//            tracker.track(TrackerFactory().trackingEvent(getString(R.string.poa_done),getString(R.string.poa_category)))
//            finish()
//        }
    }

//    override fun onResume() {
//        super.onResume()
//        tracker.trackScreen("PoA Confirmed")
//    }
}

