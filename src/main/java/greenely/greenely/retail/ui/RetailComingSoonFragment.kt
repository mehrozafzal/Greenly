package greenely.greenely.retail.ui

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import greenely.greenely.R
import greenely.greenely.databinding.RetailComingSoonFragmentBinding
import greenely.greenely.databinding.RetailStartFragmentBinding
import greenely.greenely.retail.ui.events.ClickEvents
import greenely.greenely.utils.getCurrencyFormat
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import javax.inject.Inject
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.text.Spannable
import android.text.method.MovementMethod
import android.widget.TextView
import greenely.greenely.utils.initialize


class RetailComingSoonFragment : BaseFragment() {

    private lateinit var binding: RetailComingSoonFragmentBinding


    private lateinit var viewModel: RetailViewModel

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    /**
     * Create the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_coming_soon_fragment, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailViewModel::class.java]


        binding.btnInterested.setOnClickListener {
            viewModel.onClickSendRetailInterest()
            viewModel.setOnClick(ClickEvents.COMING_SOON_TOP_INTERESTED_CLICK)
        }

        binding.btnPromoCode.setOnClickListener {
            viewModel.setOnClick(ClickEvents.COMING_SOON_PROMOCODE_TOP_BUTTON_CLICK)
        }

        binding.label3.movementMethod = LinkMovementMethod.getInstance()

//        viewModel.discount.observe(this, Observer {
//            it?.let {
//                binding.label4.setText(String.format(getString(R.string.retail_coming_soon_description_2), it.getCurrencyFormat()))
//            }
//        })

        viewModel.OnboardingInterestVisibility.observe(this, Observer {
            it?.let {
                binding.btnInterested.visibility=it
            }
        })

        prepareNotficationBadge()
    }


    private fun prepareNotficationBadge() {
        val notificationBadge = binding.notificationIcon.badge
        notificationBadge.initialize(binding.notificationIcon.layout)
    }


}

