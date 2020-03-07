package greenely.greenely.settings.ui

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.*
import coil.api.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import com.nex3z.notificationbadge.NotificationBadge
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.api.models.ErrorMessage
import greenely.greenely.databinding.SettingsFragmentBinding
import greenely.greenely.guidance.ui.GuidanceFragment
import greenely.greenely.main.ui.MainViewModel
import greenely.greenely.profile.ui.UpdateProfileFragment
import greenely.greenely.retailonboarding.ui.util.RedeemCodeFlow
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retail.models.RetailStateResponseModel
import greenely.greenely.retailinvite.ui.RetaiInviteActivity
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import greenely.greenely.settings.data.SettingsInfo
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.CommonUtils
import greenely.greenely.utils.isApiProduction
import greenely.greenely.utils.pdfview.PdfRendererView
import greenely.greenely.utils.replaceFragmentWithBottomUpAnimation
import io.intercom.android.sdk.Intercom
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Fragment for showing the setting available in the app.
 */
class SettingsFragment : androidx.fragment.app.Fragment(), SettingsBackPressedListener {

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tracker: Tracker

    private lateinit var binding: SettingsFragmentBinding

    private lateinit var viewModel: SettingsViewModel

    private lateinit var parentViewModel: MainViewModel

    private lateinit var bottomNavigationView: View

    @Inject
    lateinit var userStore: UserStore


    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        return binding.root
    }

    /**
     * Initial setup and bin to the view model.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bottomNavigationView = activity!!.findViewById(R.id.bottomNavigation)
        viewModel = ViewModelProviders.of(activity!!, factory).get(SettingsViewModel::class.java)
        parentViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        viewModel.getEvents().observe(this, Observer {
            when (it) {
                is UiEvent.PasswordChanged -> {
                    Snackbar.make(binding.content, R.string.password_changed, Snackbar.LENGTH_LONG)
                            .show()
                }
                is UiEvent.ShowError -> showErrorMessage(it.error)
            }
        })
        viewModel.getSettings().observe(this, Observer {
            it?.let {
                setSettingsInfo(it)
                setProfileData()
            }
        })

        binding.logout.setOnClickListener {
            trackEvent("clicked_logout", "Settings screen")
            parentViewModel.logout()
        }

        binding.viewModel = viewModel
        viewModel.fetchRetailState().observe(this, Observer {
            it?.let {

                var response = it
                binding.retailState = response

                if (!it.isRetailCustomer) {
                    setRedeemCodeSubTitle(it)
                    binding.redeemCodeContainer.setOnClickListener {
                        trackEvent("clicked_have_referral_code", "Settings screen")
                        navigateToRetailScreen(response)
                    }
                } else {
                    setInviteFriendView(response)
                }

            }
        })

        binding.toolbar.inflateMenu(R.menu.home_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when {
                it.itemId == R.id.intercom -> {
                    Intercom.client().displayMessenger()
                    true
                }
                else -> false
            }
        }
        prepareNotificationBadge()
        bindClickListeners()

        binding.toolbar.setNavigationOnClickListener {
            closeSettingsFragment()
        }

        binding.progress.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    private var lastVisibility = View.GONE

                    override fun onGlobalLayout() {
                        if (binding.progress.visibility != lastVisibility) {
                            if (binding.progress.visibility == View.VISIBLE) {
                                binding.content.visibility = View.INVISIBLE
                            } else {
                                binding.content.visibility = View.VISIBLE
                            }

                            lastVisibility = binding.progress.visibility
                        }
                    }
                }
        )
        viewModel.isLoadingSettingsInfo().observe(this, Observer { it?.let { setLoading(it) } })
    }

    private fun bindClickListeners() {
        binding.userAgreement.setOnClickListener {

            val termsURL: String = if (isApiProduction()) {
                getString(R.string.userterms_file_path_production)
            } else {
                getString(R.string.userterms_file_path_staging)
            }

            startActivity(Intent(context, PdfRendererView::class.java).apply { putExtra("url", termsURL) })
            trackUserTermsOpenedEvent()

        }



        binding.householdSettings.setOnClickListener {
            trackEvent("clicked_household", "Settings screen")
            fragmentManager?.replaceFragmentWithBottomUpAnimation(HouseholdSettingsFragment())
        }

        binding.notificationSettings.setOnClickListener {
            trackEvent("clicked_notifications", "Settings screen")
            fragmentManager?.replaceFragmentWithBottomUpAnimation(NotificationSettingsFragment())
        }

        binding.changePassword.setOnClickListener {
            trackEvent("clicked_change_password", "Settings screen")
            fragmentManager?.replaceFragmentWithBottomUpAnimation(ChangePasswordFragment())
        }

        binding.guidanceSettings.setOnClickListener {
            trackEvent("clicked_guidance", "Settings screen")
            fragmentManager?.replaceFragmentWithBottomUpAnimation(GuidanceFragment())
        }

        binding.settingsUserProfileContainer.setOnClickListener {
            trackEvent("clicked_guidance", "Settings screen")
            fragmentManager?.replaceFragmentWithBottomUpAnimation(UpdateProfileFragment())
        }
    }

    private fun prepareNotificationBadge() {
        val menuItem: MenuItem = binding.toolbar.menu.findItem(R.id.intercom)
        menuItem.actionView.setOnClickListener { Intercom.client().displayMessenger() }
        val notificationBadge = menuItem.actionView.findViewById(R.id.badge) as NotificationBadge
        notificationBadge.setNumber(Intercom.client().unreadConversationCount, true)
        Intercom.client().addUnreadConversationCountListener {
            notificationBadge.setNumber(Intercom.client().unreadConversationCount, true)
        }
    }

    private fun closeSettingsFragment() {
        fragmentManager?.popBackStack()
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun setSettingsInfo(settingsInfo: SettingsInfo) {
        binding.info = settingsInfo
        binding.invalidateAll()
    }

    private fun setRedeemCodeSubTitle(retailStateResponseModel: RetailStateResponseModel) {
        binding.redeemCodeTitleSub.text = String.format(getString(R.string.use_activation_code_setting_sub_label), CommonUtils.getCurrencyFormat(retailStateResponseModel.discount))
    }

    private fun setInviteFriendView(retailStateResponseModel: RetailStateResponseModel) {
        setInviteFriendSubtitle(retailStateResponseModel)

        retailStateResponseModel.referralCode?.let {
            enableInviteFriends()
        }

        retailStateResponseModel.referralCode ifNull {
            disableInviteFriends()
        }

        binding.inviteFriendContainer.setOnClickListener {
            if (retailStateResponseModel.referralCode != null) {
                trackEvent("clicked_referral_invite", "Settings screen - Enabled")
                navigateToRetailInviteScreen()
            } else
                trackEvent("clicked_referral_invite", "Settings screen - Disabled")
        }
    }

    private fun enableInviteFriends() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.inviteFriendTitle.setTextAppearance(R.style.TextAppearance_MediumSanSerifGreen)
            binding.inviteFriendSubTitle.setTextColor(resources.getColor(R.color.grey2, null))

        } else {
            binding.inviteFriendTitle.setTextAppearance(context, R.style.TextAppearance_MediumSanSerifGreen);
            binding.inviteFriendSubTitle.setTextColor(resources.getColor(R.color.grey2))
        }
    }

    private fun disableInviteFriends() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.inviteFriendTitle.setTextAppearance(R.style.TextAppearance_InviteFriendsDisable)
            binding.inviteFriendSubTitle.setTextColor(resources.getColor(R.color.grey6, null))

        } else {
            binding.inviteFriendTitle.setTextAppearance(context, R.style.TextAppearance_InviteFriendsDisable);
            binding.inviteFriendSubTitle.setTextColor(resources.getColor(R.color.grey6))
        }
    }

    private fun trackEvent(name: String, label: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Settings",
                label
        ))
    }

    private fun setInviteFriendSubtitle(retailStateResponseModel: RetailStateResponseModel) {
        binding.inviteFriendSubTitle.setText(String.format(getString(R.string.invite_friend_setting_sub_label), CommonUtils.getCurrencyFormat(retailStateResponseModel.discount)))
    }

    private fun navigateToRetailScreen(retailStateResponseModel: RetailStateResponseModel) {
        val retailOnboardConfig = RetailOnboardConfig(null, retailStateResponseModel.discount, true, RedeemCodeFlow(null))
        val intent = Intent(context, RetailOnboardingActivity::class.java).apply {
            putExtra(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
        }
        activity?.startActivityForResult(intent, RetailOnboardingActivity.CLOSE_ONBOARDING_ACTIVITY_REQUEST_CODE)
        activity?.overridePendingTransition(R.anim.slide_up_animation, R.anim.no_change)
    }

    private fun navigateToRetailInviteScreen() {
        startActivity(Intent(context, RetaiInviteActivity::class.java))
    }

    /** bottomNavigationView.visibility = View.VISIBLE
     * Track resume event to analytics.
     */
    override fun onResume() {
        super.onResume()
        bottomNavigationView.visibility = View.GONE
        tracker.trackScreen("Settings")
    }

    /**
     * Inject before attaching to the [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is HttpException -> {
                ErrorMessage.fromError(error)?.let { errorMessage ->
                    Snackbar.make(binding.content, errorMessage.description, Snackbar.LENGTH_LONG).show()
                }
            }
            is IOException -> {
                Snackbar.make(binding.content, R.string.network_error_body, Snackbar.LENGTH_LONG)
                        .show()
            }
            else -> {
                Snackbar.make(binding.content, R.string.unexpected_error, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            binding.progress.visibility = View.VISIBLE
            binding.content.visibility = View.INVISIBLE
        } else {
            binding.progress.visibility = View.GONE
            binding.content.visibility = View.VISIBLE

        }
    }

    private fun trackUserTermsOpenedEvent() {
        tracker.track(
                TrackerFactory().trackingEvent(
                        "user_terms_opened",
                        "Settings",
                        "Settings screen"
                ))
    }

    override fun handleBackPressed() {
        closeSettingsFragment()
    }

    infix fun Any?.ifNull(block: () -> Unit) {
        if (this == null) block()
    }

    @SuppressLint("SetTextI18n")
    private fun setProfileData() {
        if (!userStore.firstName.equals("")) {
            binding.settingsProfileUsername.visibility = View.VISIBLE
            binding.settingsProfileUserEmail.visibility = View.VISIBLE
            binding.settingsProfileUserEmail.text = binding.info?.email
            binding.settingsProfileUsername.text = userStore.firstName + " " + userStore.lastName
            if (userStore.avatar.equals("")) {
                binding.settingsLetterContainer.visibility = View.VISIBLE
                binding.profileUserImage.visibility = View.GONE
                binding.itemFriendNameTag.text = userStore.firstName?.get(0).toString().trim().toUpperCase()
            } else {
                binding.profileUserImage.visibility = View.VISIBLE
                binding.profileUserImage.load(userStore.avatar)
                {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }
        } else {
            binding.settingsProfileUsername.visibility = View.GONE
            binding.settingsProfileUserEmail.visibility = View.GONE
            binding.settingsUserProfileEmail.visibility = View.VISIBLE
            binding.settingsUserProfileEmail.text = binding.info?.email
            binding.itemFriendNameTag.text = binding.info?.email?.get(0).toString().trim().toUpperCase()
        }
    }
}

