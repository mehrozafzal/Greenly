package greenely.greenely.splash.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import com.tmall.ultraviewpager.UltraViewPager
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.SplashIntroductionActivityBinding
import greenely.greenely.extensions.dp
import greenely.greenely.splash.ui.navigation.SplashNavigationHandler
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject


@Suppress("DEPRECATION")
class IntroductionActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var navigationHandler: SplashNavigationHandler

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: SplashViewModel

    private lateinit var binding: SplashIntroductionActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_introduction_activity)

        viewModel = ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)
        tracker.reset()
        viewModel.resetUserId()

        binding.viewModel = viewModel
        viewModel.navigate.observe(this, Observer {
            it?.let { navigationHandler.navigateTo(it) }
        })

        initViewPager()

        binding.ultraViewpager.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING -> trackEvent("changed_value_screen")
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                trackScreenValue(position)
            }

        })
        binding.login.setOnClickListener({
            viewModel.onClickLogin()
            trackEvent("clicked_login")
        })
        binding.createAccount.setOnClickListener({
            viewModel.onClickRegister()
            trackEvent("create_account")
        })
        trackScreenValue(0)
        trackFirstTimeUser()
    }

    private fun initViewPager() {

        binding.ultraViewpager.adapter = IntroAdapter()
        binding.ultraViewpager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        binding.ultraViewpager.initIndicator()
        binding.ultraViewpager.indicator
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setRadius((
                            TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, this.dp(2),
                                    resources.displayMetrics
                            )
                        ).toInt()
                )
                .setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                .setFocusColor(resources.getColor(R.color.white))
                .setNormalColor(resources.getColor(R.color.indicator_blue))
                .setMargin(2, 5, 2, 2)
                .setIndicatorPadding(20)
                .build()

    }

    private fun trackScreenValue(position: Int) {
        when (position) {
            0 -> tracker.trackScreen("Start Hourly Data")
            1 -> tracker.trackScreen("Start Comparison")
            2 -> tracker.trackScreen("Start Warnings and Reports")
            3 -> tracker.trackScreen("Start Reduce Cost")
        }
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, getString(R.string.intro_tracking_catagory)))
    }

    private fun trackFirstTimeUser() {
        if (!viewModel.checkFirstOpenFlag()) {
            tracker.track(TrackerFactory().trackingEvent("first_seen", getString(R.string.intro_tracking_catagory)))
            viewModel.setOpenedFlag()
        }
    }


}
