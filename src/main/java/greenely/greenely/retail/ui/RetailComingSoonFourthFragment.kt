package greenely.greenely.retail.ui

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
import greenely.greenely.databinding.RetailComingSoonFourthViewBinding
import greenely.greenely.databinding.RetailComingSoonFragmentBinding
import greenely.greenely.retail.ui.events.ClickEvents
import greenely.greenely.utils.getCurrencyFormat
import javax.inject.Inject

class RetailComingSoonFourthFragment : BaseFragment() {

    private lateinit var binding: RetailComingSoonFourthViewBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_coming_soon_fourth_view, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        viewModel = ViewModelProviders.of(activity!!, factory)[RetailViewModel::class.java]
        binding.btnInterested.setOnClickListener {
            viewModel.onClickSendRetailInterest()
            viewModel.setOnClick(ClickEvents.COMING_SOON_BOTTOM_INTERESTED_CLICK)
        }

        binding.btnPromoCode.setOnClickListener {

            viewModel.setOnClick(ClickEvents.COMING_SOON_PROMOCODE_BOTTOM_BUTTON_CLICK)
        }

        viewModel.OnboardingInterestVisibility.observe(this, Observer {
            it?.let {
                binding.btnInterested.visibility = it
            }
        })

    }


}