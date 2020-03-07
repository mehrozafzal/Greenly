package greenely.greenely.welcome.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dagger.android.AndroidInjection
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.accountsetup.MainAccountSetupNextHandler
import greenely.greenely.databinding.ActivityWelcomeBinding
import greenely.greenely.login.ui.LoginActivity
import greenely.greenely.models.Resource
import greenely.greenely.push.PushRegistrationIntentService
import greenely.greenely.registration.ui.RegistrationActivity
import greenely.greenely.splash.ui.SplashActivity
import greenely.greenely.store.SharedPreferencesUserStore
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.utils.GreenelySingleton
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import javax.inject.Inject

@OpenClassOnDebug
class WelcomeActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: WelcomeViewModel

    private lateinit var binding: ActivityWelcomeBinding

    private val TAG: String = "WelcomeActivity"

    @Inject
    lateinit var userStore: UserStore


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory).get(WelcomeViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.viewModel = viewModel
        bindViews()
    }


    private fun bindViews() {

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val invitedUsername = bundle.getString(SplashActivity.invitedUserKey)
            if (!invitedUsername.equals(SplashActivity.expiredKeyValue)) {
                binding.activityWelcomeTitle.text = (resources.getString(R.string.welcomeActivity_title) + "$invitedUsername" + "?")
                binding.activityWelcomeSubtitle.text = (resources.getString(R.string.welcomeActivity_subtitle))
                binding.activityWelcomeBottomText.visibility = View.VISIBLE
                userStore.isInvitedUser = true
                userStore.invitedUserName = invitedUsername
                binding.activityWelcomeRegister.setTextAppearance(this, R.style.TextAppearance_Body4)
                binding.activityWelcomeRegister.setTextColor(resources.getColor(R.color.green_3))
            } else {
                binding.activityWelcomeTitle.text = (resources.getString(R.string.welcomeActivity_titleExpired))
                binding.activityWelcomeSubtitle.text = (resources.getString(R.string.welcomeActivity_subtitle))
                binding.activityWelcomeBottomText.visibility = View.VISIBLE
                userStore.isInvitedUser = true
                userStore.invitedUserName = invitedUsername
                binding.activityWelcomeRegister.setTextAppearance(this, R.style.TextAppearance_Body4)
                binding.activityWelcomeRegister.setTextColor(resources.getColor(R.color.green_3))
            }

        } else {
            binding.activityWelcomeTitle.text = (resources.getString(R.string.welcomeActivity_title_new_user))
            binding.activityWelcomeSubtitle.text = (resources.getString(R.string.welcomeActivity_subtitle_new_user))
            binding.activityWelcomeBottomText.visibility = View.GONE
            userStore.isInvitedUser = false
            binding.activityWelcomeRegister.setTextAppearance(this, R.style.TextAppearance_Body6)
            binding.activityWelcomeRegister.setTextColor(resources.getColor(R.color.green_3))
        }

        binding.activityWelcomeLogin.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        })

        binding.activityWelcomeRegister.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        })

    }

    private fun registerPushAsync() {
        PushRegistrationIntentService.enqueueWork(this, Intent())
    }


    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Welcome")
    }

    override fun onStart() {
        super.onStart()
        viewModel.autoLogin()
    }


}
