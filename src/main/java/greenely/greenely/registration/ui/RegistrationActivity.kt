package greenely.greenely.registration.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.accountsetup.AccountSetupNextHandler
import greenely.greenely.accountsetup.MainAccountSetupNextHandler
import greenely.greenely.databinding.RegistrationActivityBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.models.Resource
import greenely.greenely.profile.model.Account
import greenely.greenely.push.PushRegistrationIntentService
import greenely.greenely.registration.ui.events.Event
import greenely.greenely.registration.ui.events.RegistrationEventHandler
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.OverlappingLoaderFactory
import greenely.greenely.utils.isApiProduction
import greenely.greenely.utils.pdfview.PdfRendererView
import javax.inject.Inject

@OpenClassOnDebug
class RegistrationActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private val navigationHandler: AccountSetupNextHandler by lazy {
        MainAccountSetupNextHandler(this)
    }

    @Inject
    lateinit var errorHandler: RegistrationErrorHandler

    @Inject
    lateinit var loadingHandlerFactory: OverlappingLoaderFactory

    @Inject
    lateinit var eventHandler: RegistrationEventHandler

    @Inject
    lateinit var userStore: UserStore

    @Inject
    lateinit var accountsMap: HashMap<Int, Account>

    @VisibleForTesting
    internal val loadingHandler by lazy {
        loadingHandlerFactory.createLoadingHandler(binding.progress, binding.done)
    }

    private lateinit var viewModel: RegistrationViewModel

    private lateinit var binding: RegistrationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, factory).get(RegistrationViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.registration_activity)

        binding.viewModel = viewModel

        binding.checkUserAgreement.setOnClickListener {

            val termsURL: String = if (isApiProduction()) {
                getString(R.string.userterms_file_path_production)
            } else {
                getString(R.string.userterms_file_path_staging)
            }

            startActivity(Intent(this, PdfRendererView::class.java).apply { putExtra("url", termsURL) })
            trackUserTermsOpenedEvent()
        }

        viewModel.events.observe(this, Observer {
            when (it) {
                is Event.Exit -> finish()
                is Event.HideKeyboard -> hideKeyboard()
            }
        })

        viewModel.authenticationInfo.observe(this, Observer {
            it?.let {
                if (it is Resource.Loading) loadingHandler.loading = true
                else {
                    loadingHandler.loading = false
                    when (it) {
                        is Resource.Success -> {
                            registerPushAsync()
                            trackEvent("signed_up")
                            tracker.identify(it.value.userIdentification)
                            navigationHandler.navigateTo(it.value.accountSetupNext)
                            /** Add new account details to map **/
                            val accountsMap: HashMap<Int, Account> = userStore.getAccountsMapFromPreference()
                            val account = Account("", binding.emailEditText.text.toString(), "", it.value.userId, userStore.token, "")
                            accountsMap[it.value.userId] = account
                            userStore.storeAccountsMapToPreference(accountsMap)
                        }
                        is Resource.Error -> {
                            errorHandler.handleError(it.error)
                        }
                    }
                }
            }
        })

        handleClickListeners()
    }


    private fun handleClickListeners() {
        binding.registrationActivityClose.setOnClickListener {
            finish()
        }

        binding.done.setOnClickListener {
            if (userStore.isLinkExpired == false) {
                if (!userStore.invitationID.equals("")) {
                    userStore.isRegisteredUser = true
                    viewModel.register(userStore.invitationID.toString())
                } else {
                    viewModel.register(null)
                }
            } else {
                viewModel.register(null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Registration")
    }

    private fun registerPushAsync() {
        PushRegistrationIntentService.enqueueWork(this, Intent())
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, "Registration"))
    }

    fun trackUserTermsOpenedEvent() {
        tracker.track(
                TrackerFactory().trackingEvent(
                        "user_terms_opened",
                        "Registration",
                        "Registration screen"
                ))
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }
}
