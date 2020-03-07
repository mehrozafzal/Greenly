package greenely.greenely.retailinvite.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.ReferralInviteQaFragmentBinding
import greenely.greenely.tracking.Tracker
import greenely.greenely.utils.CommonUtils
import javax.inject.Inject

class ReferralInviteQAFragment: androidx.fragment.app.Fragment()  {


    private lateinit var binding: ReferralInviteQaFragmentBinding

//    private lateinit var eventHandler: EventHandler

    private val viewModel: RetailInviteViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[RetailInviteViewModel::class.java]
    }

    @Inject
    lateinit var tracker: Tracker

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        eventHandler = eventHandlerFactory.createEventHandler(binding)
//        viewModel.events.observe(this, Observer { it?.let { eventHandler.handleEvent(it) } })


        if(viewModel.referralInviteResponseModel.value==null) viewModel.fetchRetailReferral()

        viewModel.referralInviteResponseModel.observe(this, Observer {
            it?.let {

                var discount=CommonUtils.getCurrencyFormat(it.discount)
                binding.inviteQa.setText(String.format(getString(R.string.invite_friends_qa),discount,discount))
            }
        })

        setToolBarNavigation()

    }

    fun setToolBarNavigation(){
        binding.toolbar.setNavigationIcon(R.drawable.arrow_back_green)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.referral_invite_qa_fragment, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Referral Invite Friend QA")
    }
}