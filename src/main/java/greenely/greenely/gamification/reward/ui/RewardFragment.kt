package greenely.greenely.gamification.reward.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.FragmentRewardBinding
import greenely.greenely.store.UserStore
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import javax.inject.Inject


class RewardFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: RewardFragmentViewModel

    private lateinit var binding: FragmentRewardBinding

    @Inject
    lateinit var userStore: UserStore


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reward, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[RewardFragmentViewModel::class.java]
        bindViews()
    }

    private fun bindViews() {
        val rewardListAdapter = context?.let { RewardListAdapter(it) }
        binding.rewardFragmentRv.adapter = rewardListAdapter
        binding.rewardFragmentRv.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun trackEvent(name: String) {
        tracker.track(TrackerFactory().trackingEvent(name, "Reward"))
    }

}
