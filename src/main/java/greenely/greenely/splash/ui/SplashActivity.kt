package greenely.greenely.splash.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.appsflyer.AppsFlyerLib
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import dagger.android.AndroidInjection
import greenely.greenely.BuildConfig
import greenely.greenely.R
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.accountsetup.MainAccountSetupNextHandler
import greenely.greenely.databinding.SplashScreenBinding
import greenely.greenely.main.ui.MainViewModel
import greenely.greenely.models.Resource
import greenely.greenely.push.PushRegistrationIntentService
import greenely.greenely.push.models.MessageType
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.welcome.ui.WelcomeActivity
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    companion object Constants {
        const val invitedUserKey: String = "INVITED_USER_NAME"
        const val expiredKeyValue: String = "KEY_EXPIRED"
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var userStore: UserStore

    private lateinit var binding: SplashScreenBinding

    private val STAGING = "staging"

    private val MOCK = "mock"

    private val PRODUCTION = "production"

    private val TAG: String = "SplashActivity"

    private lateinit var inviteUsername: String

    private var isDeepLinkCall: Boolean = false

    private lateinit var pendingDynamicLinkData: PendingDynamicLinkData


    @VisibleForTesting
    val accountSetupNextHandler: AccountSetupNextHandler by lazy {
        MainAccountSetupNextHandler(this)
    }

    private val viewModel: SplashViewModel by lazy {
        ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("deep_link")) {
                trackEvent("Push Notification Tapped")
                Log.d("notificationdebug", "notification has been tapped!")
            }
        }

        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen)

        if (!BuildConfig.DEBUG) setUpAppsFlyer()

        viewModel.identification.observe(this, Observer {
            it?.let {
                tracker.identify(it)
            }
            registerPushAsync()
        })

        viewModel.accountSetupNext.observe(this, Observer {
            it?.let {
                accountSetupNextHandler.navigateTo(it)
            }
        })

        viewModel.setupNext.observe(this, Observer {
            it?.let {
                accountSetupNextHandler.navigateTo(it)
            }
        })

        viewModel.finishSplashDuration.observe(this, Observer {
            it?.let {
                if (it) {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        })

        viewModel.openFirebaseLink.observe(this, Observer {
            it?.let {
                if (it) {
                    if (!BuildConfig.DEBUG) {
                        var deepLink: Uri? = null
                        isDeepLinkCall = true
                        deepLink = pendingDynamicLinkData.link
                        RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.production_base_url))
                        viewModel.getInvitedFriend(deepLink.lastPathSegment.toString())
                    } else {
                        var deepLink: Uri? = null
                        isDeepLinkCall = true
                        deepLink = pendingDynamicLinkData.link
                        RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.staging_base_url))
                        viewModel.getInvitedFriend(deepLink.lastPathSegment.toString())
                    }
                }
            }
        })

        handleDeepLinkIntent()


        if (BuildConfig.DEBUG) {
            RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.staging_base_url))
        } else {
            RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.production_base_url))
        }

        bindViews()
    }

    private fun handleFirebaseDeepLink() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(intent)
                .addOnSuccessListener(this) {
                    if (it != null) {
                        userStore.invitationID = it.link.lastPathSegment
                        pendingDynamicLinkData = it
                        isDeepLinkCall = true
                        userStore.isInvitedUser = true
                        viewModel.checkLogin()
                    } else {
                        userStore.clearInvitedUserPreference()
                        viewModel.autoLogin()
                    }
                }
                .addOnFailureListener(this) {
                        viewModel.autoLogin()
                }
    }

    private fun bindViews() {
        viewModel.inviteFriendLiveData.observe(this, Observer {

            when (it) {
                is Resource.Success -> {
                    userStore.isLinkExpired = false
                    Log.d(TAG, "INVITED_FRIEND:${it.value.friend_alias}")
                    userStore.invitedUserName = it.value.friend_alias
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.putExtra(invitedUserKey, it.value.friend_alias)
                    startActivity(intent)
                    finish()
                }

                is Resource.Error -> {
                    userStore.isLinkExpired = true
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.putExtra(invitedUserKey, expiredKeyValue)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    private fun loadDebugMenu() {

        mainViewModel.logout()

        var selectedApiEndpoint = ""

        binding.splashImage.visibility = View.GONE

        binding.debugMenuLayout.visibility = View.VISIBLE

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.stagingButton -> {
                    selectedApiEndpoint = STAGING
                }
                R.id.mockButton -> {
                    selectedApiEndpoint = MOCK
                }
                R.id.productionButton -> {
                    selectedApiEndpoint = PRODUCTION
                }
            }
        }

        binding.confirmEndpointButton.setOnClickListener {
            when {
                selectedApiEndpoint.equals(STAGING) -> RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.staging_base_url))
                selectedApiEndpoint.equals(MOCK) -> RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.mock_postman_base_url))
                selectedApiEndpoint.equals(PRODUCTION) -> RetrofitUrlManager.getInstance().setGlobalDomain(getString(R.string.production_base_url))
            }
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        handleFirebaseDeepLink()
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Splash")
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_down)
    }

    private fun setUpAppsFlyer() {
        AppsFlyerLib.getInstance().startTracking(
                application,
                getString(R.string.appsflyer_dev_key)
        )
    }

    private fun registerPushAsync() {
        PushRegistrationIntentService.enqueueWork(this, Intent())
    }

    private fun handleDeepLinkIntent() {

        try {
            val intentData: MessageType? = intent.extras?.getSerializable("deep_link")?.let { it as MessageType }
            intentData?.let {
                viewModel.setDeepLinkScreenName(it.name.toLowerCase())
                return
            }

            val intentIntercomData: Uri? = intent?.data
            if (intentIntercomData?.host != null) {
                viewModel.setDeepLinkScreenName(intentIntercomData.host!!)
            } else {
                viewModel.setDeepLinkScreenName("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun trackEvent(label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                "push_notification_tapped",
                "Push Notification",
                label
        ))
    }
}
