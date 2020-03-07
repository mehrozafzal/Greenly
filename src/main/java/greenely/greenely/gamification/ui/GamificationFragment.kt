package greenely.greenely.gamification.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.competefriend.ui.CompeteFriendFragment
import greenely.greenely.databinding.GamificationFragmentBinding
import greenely.greenely.gamification.achievement.ui.AchievementFragment
import greenely.greenely.gamification.reward.ui.RewardFragment
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject


class GamificationFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: GamificationViewModel

    private lateinit var binding: GamificationFragmentBinding

    @Inject
    lateinit var userStore: UserStore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.gamification_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[GamificationViewModel::class.java]
        bindViews()
    }

    private fun bindViews() {
        val gamificationPagerAdapter = GamificationPagerAdapter(childFragmentManager)
        gamificationPagerAdapter.addFragment("Vänner", CompeteFriendFragment())
        gamificationPagerAdapter.addFragment("Prestationer", AchievementFragment())
        gamificationPagerAdapter.addFragment("Belöningar", RewardFragment())
        binding.gamificationFragmentPager.adapter = gamificationPagerAdapter
        binding.gamificationFragmentPager.offscreenPageLimit = 3
        binding.gamificationFragmentTabLayout.setupWithViewPager(binding.gamificationFragmentPager)
        binding.gamificationFragmentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, "Gamification"))
    }


}
