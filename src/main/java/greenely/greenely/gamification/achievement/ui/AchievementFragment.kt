package greenely.greenely.gamification.achievement.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.competefriend.ui.CompeteFriendFragment
import greenely.greenely.databinding.FragmentAchievementBinding
import greenely.greenely.databinding.GamificationFragmentBinding
import greenely.greenely.extensions.setDivider
import greenely.greenely.gamification.ui.GamificationPagerAdapter
import greenely.greenely.gamification.ui.GamificationViewModel
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject

class AchievementFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: AchievementViewModel

    private lateinit var binding: FragmentAchievementBinding

    @Inject
    lateinit var userStore: UserStore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_achievement, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[AchievementViewModel::class.java]
        bindViews()
    }

    private fun bindViews() {
        val achievementListAdapter = context?.let { AchievementListAdapter(it) }
        binding.achievementFragmentRv.adapter = achievementListAdapter
        binding.achievementFragmentRv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        binding.achievementFragmentRv.setDivider(R.drawable.friend_list_divider)

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, "Achievement"))
    }


}
