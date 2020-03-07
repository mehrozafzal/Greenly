package greenely.greenely.competefriend.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.competefriend.errors.CompeteFriendErrorHandler
import greenely.greenely.competefriend.json.rankListJson.FriendsItemJson
import greenely.greenely.competefriend.json.rankListJson.RankResponseJson
import greenely.greenely.databinding.CompeteFriendFragmentBinding
import greenely.greenely.databinding.DialogDeleteFriendBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.extensions.setDivider
import greenely.greenely.guidance.ui.latestsolaranalysis.LatestSolarAnalysisActivity
import greenely.greenely.models.Resource
import greenely.greenely.settings.ui.SettingsFragment
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.replaceFragmentWithHorizontalSlideAnimation
import javax.inject.Inject


class CompeteFriendFragment : androidx.fragment.app.Fragment(), HasSnackbarView, CompeteFriendHelper {

    override val snackbarView: View
        get() = binding.coordinatorLayout

    @Inject
    lateinit var errorHandler: CompeteFriendErrorHandler

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: CompeteFriendViewModel

    private lateinit var binding: CompeteFriendFragmentBinding


    private lateinit var friendListAdapter: FriendsAdapter

    @Inject
    lateinit var userStore: UserStore

    private var listPosition: Int = -1

    private var KILO_WATT_RESOLUTION: String = "kwh_per_squared_meter"
    private var TIME_RESOLUTION: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.compete_friend_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[CompeteFriendViewModel::class.java]
        bindViews()
        //TODO: test purpose
        //showOnboardingScreen(true)
    }

    private fun bindViews() {

        viewModel.errors.observe(this, Observer {
            it?.let {
                errorHandler.handleError(it)
            }
        })



        binding.competeFriendFriendsRv.setDivider(R.drawable.friend_list_divider)


        viewModel.rankResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.loader.hide()
                    if (it.value.friends != null) {
                        toggleViews(false)
                        if (it.value.friends.isNotEmpty()) {
                            binding.competeFriendRankingListFooterContainer.visibility = View.VISIBLE
                            toggleViews(true)
                            populateFriendList(it.value)
                            if (userStore.isOnboardingShown == false && userStore.isInvitedUser == true) {
                                userStore.isInvitedUser = false
                                userStore.isOnboardingShown = true
                                showOnboardingScreen(true)
                            }
                        } else {
                            userStore.isOnboardingShown = true
                            toggleViews(false)
                        }
                    }
                }
                is Resource.Loading -> binding.loader.show()
                is Resource.Error -> {
                    binding.loader.hide()
                    errorHandler.handleError(it.error)
                    userStore.isOnboardingShown = true
                }
            }
        })


        viewModel.inviteUserResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.loader.hide()
                    shareLinkWithUser(it.value.invitationLink)
                }

                is Resource.Loading -> binding.loader.show()
                is Resource.Error -> {
                    binding.loader.hide()
                    errorHandler.handleError(it.error)
                }
            }
        })


        viewModel.events.observe(this, Observer
        {
            if (it != null)
                friendListAdapter.removeFriendFromList(listPosition)
        })

        viewModel.error.observe(this, Observer {
            if (it != null)
                errorHandler.handleError(it)
        })


        viewModel.addFriendResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    binding.loader.hide()
                    Log.d("CompeteFriendFragment", it.toString())
                    fetchInitialRankingList()
                }

                is Resource.Loading ->
                    binding.loader.show()
                is Resource.Error -> {
                    binding.loader.hide()
                    errorHandler.handleError(it.error)
                    fetchInitialRankingList()
                }
            }
        })

        clickListeners()
    }

    private fun initiateRankListCall() {
        //Handle Invitation Route
        if (!userStore.invitationID.equals("") && userStore.isLinkExpired == false && userStore.isInvitedUser == true) {
            if (userStore.isRegisteredUser == false) {
                userStore.invitationID?.let { viewModel.fetchAddFriendResponse(it) }
                userStore.invitationID = ""
                userStore.isInvitedUser = false
            } else {
                userStore.isRegisteredUser = false
                userStore.isOnboardingShown = false
                userStore.invitationID = ""
                fetchInitialRankingList()
            }
        } else {
            userStore.isLinkExpired = false
            fetchInitialRankingList()
            userStore.invitationID = ""
        }
    }

    private fun clickListeners() {
        binding.competeFriendSettingsIcon.setOnClickListener {
            fragmentManager?.replaceFragmentWithHorizontalSlideAnimation(SettingsFragment())
        }

        binding.competeFriendInfo.setOnClickListener {
            trackEvent("clicked_compete_faq")
            startActivity(Intent(context, InfoActivity::class.java))
        }

        binding.competeFriendInviteUserBtn.setOnClickListener {
            viewModel.fetchInviteUserResponse()
        }

        binding.competeFriendNotNow.setOnClickListener {
            //binding.competeFriendOnboardScreen.visibility = View.GONE
            showOnboardingScreen(false)
        }

        binding.competeFriendRankingListFooter.competeFriendInviteUserSecondBtn.setOnClickListener {
            onShareBtnClicked()
        }

        binding.competeFriendOverlayFooterTxt.setOnClickListener {
            onSignPoaClicked()
        }
    }

    private fun fetchInitialRankingList() {
        binding.months.isChecked = true
        binding.months.setTextColor(resources.getColor(R.color.white))
        binding.kwhM2.isChecked = true
        binding.kwhM2.setTextColor(resources.getColor(R.color.white))
        viewModel.fetchRankedUserList("monthly", KILO_WATT_RESOLUTION)
        setResolutionListener()
        setKiloWattResolutionListener()
    }

    private fun showOnboardingScreen(boolean: Boolean) {
        if (boolean) {
            binding.competeFriendOnboardingScreenHeader.visibility = View.VISIBLE
            binding.competeFriendOnboardingScreenSubHeader.visibility = View.VISIBLE
            binding.competeFriendOnboardScreenHole.visibility = View.VISIBLE
            binding.competeFriendOnboardingScreenFooter.visibility = View.VISIBLE
            binding.competeFriendOnboardSubTitle.text = "Nu är du och " + userStore.invitedUserName + "\nvänner på Greenely"
            binding.competeFriendPoaClick.setOnClickListener {
                onSignPoaClicked()
            }
        } else {
            // binding.competeFriendOnboardScreen.visibility = View.GONE
            binding.competeFriendOnboardingScreenHeader.visibility = View.GONE
            binding.competeFriendOnboardingScreenSubHeader.visibility = View.GONE
            binding.competeFriendOnboardScreenHole.visibility = View.GONE
            binding.competeFriendOnboardingScreenFooter.visibility = View.GONE
        }

    }

    private fun setResolutionListener() {

        binding.timeResolution.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.months) {
                TIME_RESOLUTION = "monthly"
                viewModel.fetchRankedUserList("monthly", KILO_WATT_RESOLUTION)
                binding.months.setTextColor(resources.getColor(R.color.white))
                binding.week.setTextColor(resources.getColor(R.color.green_3))
                trackEvent("changed_compete_resolution")

            } else {
                TIME_RESOLUTION = "weekly"
                viewModel.fetchRankedUserList("weekly", KILO_WATT_RESOLUTION)
                binding.months.setTextColor(resources.getColor(R.color.green_3))
                binding.week.setTextColor(resources.getColor(R.color.white))
                trackEvent("changed_compete_resolution")
            }
        }
    }


    private fun setKiloWattResolutionListener() {

        binding.kwhResolution.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.kwhM2) {
                KILO_WATT_RESOLUTION = "kwh_per_squared_meter"
                viewModel.fetchRankedUserList(TIME_RESOLUTION, KILO_WATT_RESOLUTION)
                binding.kwhM2.setTextColor(resources.getColor(R.color.white))
                binding.kwh.setTextColor(resources.getColor(R.color.green_3))
            } else {
                KILO_WATT_RESOLUTION = "kwh_per_person"
                viewModel.fetchRankedUserList(TIME_RESOLUTION, KILO_WATT_RESOLUTION)
                binding.kwhM2.setTextColor(resources.getColor(R.color.green_3))
                binding.kwh.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun populateFriendList(rankResponseJson: RankResponseJson) {

        binding.competeFriendListTitle.text = rankResponseJson.title
        val friendsList: MutableList<FriendsItemJson> = rankResponseJson.friends as MutableList<FriendsItemJson>
        friendListAdapter = FriendsAdapter(context!!, friendsList, this)
        binding.competeFriendFriendsRv.adapter = friendListAdapter
        binding.competeFriendFriendsRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        binding.competeFriendFriendsRv.setHasFixedSize(true)
    }

    private fun toggleViews(showList: Boolean) {
        if (showList) {
            binding.competeFriendListContainer.visibility = View.VISIBLE
            binding.competeFriendInviteUserContainer.visibility = View.GONE
            binding.competeFriendSettingsIcon.visibility = View.VISIBLE
        } else {
            binding.competeFriendListContainer.visibility = View.GONE
            binding.competeFriendInviteUserContainer.visibility = View.VISIBLE
            binding.competeFriendSettingsIcon.visibility = View.GONE
        }
    }

    private fun shareLinkWithUser(dynamicUrl: String?) {
        trackEvent("add_compete_friend")
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_url_text))
        i.putExtra(Intent.EXTRA_TEXT, dynamicUrl)
        startActivity(Intent.createChooser(i, "Share URL"))
    }

    private fun startSolarAnalysis() {
        trackSolarAnalysis()
        startActivity(Intent(context, SolarAnalysisActivity::class.java))
    }

    private fun goToLatestAnalysis() {
        tracker.track(TrackerFactory().trackingEvent("sa_previous_result", getString(R.string.solar_analysis_tracking_category)))
        startActivity(Intent(context, LatestSolarAnalysisActivity::class.java))
    }

    private fun trackSolarAnalysis() {
        tracker.track(TrackerFactory().trackingEvent("sa_started", getString(R.string.solar_analysis_tracking_category)))
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Compete")
        initiateRankListCall()
    }

    override fun onShareBtnClicked() {
        trackEvent("add_compete_friend")
        viewModel.fetchInviteUserResponse()
    }

    override fun onItemClicked(listPosition: Int, friendsItemJson: FriendsItemJson) {
        this.listPosition = listPosition
        trackEvent("clicked_on_friend")
        if (friendsItemJson.friendId != null)
            showDialog(friendsItemJson)
    }

    override fun onSignPoaClicked() {
        startActivity(Intent(context, SignatureActivity::class.java))
        showOnboardingScreen(false)
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, "Compete"))
    }

    private fun showDialog(friendsItemJson: FriendsItemJson) {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val binding: DialogDeleteFriendBinding = DialogDeleteFriendBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        binding.dialogFriendOk.setOnClickListener {
            viewModel.deleteFriend(friendsItemJson.friendId.toString())
            trackEvent("removed_a_friend")
            dialog.dismiss()
        }
        binding.dialogFriendCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}
