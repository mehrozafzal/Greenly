package greenely.greenely.login.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import dagger.android.AndroidInjection
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.accountsetup.MainAccountSetupNextHandler
import greenely.greenely.databinding.LoginActivityBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.login.ui.events.LoginEventHandler
import greenely.greenely.models.AuthenticationInfo
import greenely.greenely.models.Resource
import greenely.greenely.profile.model.Account
import greenely.greenely.push.PushRegistrationIntentService
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.OverlappingLoaderFactory
import greenely.greenely.utils.isApiProduction
import javax.inject.Inject

@OpenClassOnDebug
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    lateinit var navigationHandler: AccountSetupNextHandler

    @Inject
    lateinit var errorHandler: LoginErrorHandler

    @Inject
    lateinit var loadingHandlerFactory: OverlappingLoaderFactory

    @Inject
    lateinit var eventHandler: LoginEventHandler

    @Inject
    lateinit var userStore: UserStore

    @VisibleForTesting
    internal val loadingHandler by lazy {
        loadingHandlerFactory.createLoadingHandler(
                binding.progress,
                binding.login
        )
    }

    private lateinit var binding: LoginActivityBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        navigationHandler = MainAccountSetupNextHandler(this)

        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        binding.viewModel = viewModel

        if (!isApiProduction()) {
            viewModel.loginData.email = "test141@test.com"
            viewModel.loginData.password = "test123"
        }

        /*   if (!isApiProduction()) {
               viewModel.loginData.email = "danny.hajj+r10@greenely.se"
               viewModel.loginData.password = "123123"
           }*/

        /*   if (isApiProduction() && BuildConfig.DEBUG) {
                  viewModel.loginData.email = "fredrik.hagblom@greenely.se"
                  viewModel.loginData.password = "test123"
              }*/

        viewModel.loginResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    loadingHandler.loading = false
                    navigationHandler.navigateTo(it.value.accountSetupNext)
                    tracker.identify(it.value.userIdentification)
                    trackEvent("logged_in")
                    userStore.isLoggedIn = true
                    registerPushAsync()
                    updateAccountsMap(it.value)
                }
                is Resource.Loading -> loadingHandler.loading = true
                is Resource.Error -> {
                    loadingHandler.loading = false
                    errorHandler.handleError(it.error)
                }
            }
        })

        viewModel.events.observe(this, Observer {
            it?.let { eventHandler.handleEvent(it) }
        })

        binding.forgotPassword.setOnClickListener {
            viewModel.forgotPassword()
            trackEvent("reset_password")
        }

        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
            trackEvent("back_from_login")
        }
    }

    private fun updateAccountsMap(authenticationInfo: AuthenticationInfo) {
        /** Add new account details to map **/
        val accountsMap: HashMap<Int, Account> = userStore.getAccountsMapFromPreference()
        if (!accountsMap.containsKey(authenticationInfo.userId)) {
            val account = Account("", binding.emailTextInput.text.toString(), "", authenticationInfo.userId, userStore.token, "")
            accountsMap[authenticationInfo.userId] = account
            userStore.storeAccountsMapToPreference(accountsMap)
        } else {
            val account: Account? = accountsMap[authenticationInfo.userId]
            account?.token = userStore.token
            account?.let { it1 -> accountsMap.put(authenticationInfo.userId, it1) }
            userStore.storeAccountsMapToPreference(accountsMap)
        }
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Login")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerPushAsync() {
        PushRegistrationIntentService.enqueueWork(this, Intent())
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, getString(R.string.login_tracking_catagory)))
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }

}
