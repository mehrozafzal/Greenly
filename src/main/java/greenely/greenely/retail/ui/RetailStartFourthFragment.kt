package greenely.greenely.retail.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import greenely.greenely.R
import greenely.greenely.databinding.RetailStartFourthViewBinding
import greenely.greenely.databinding.RetailStartThirdViewBinding
import greenely.greenely.retail.ui.events.ClickEvents
import javax.inject.Inject

class RetailStartFourthFragment : BaseFragment() {

    private lateinit var binding: RetailStartFourthViewBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_start_fourth_view, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailViewModel::class.java]


        binding.btnContinue.setOnClickListener {
            viewModel.setOnClick(ClickEvents.RETAIL_START_BOTTOM_CONTINUE_CLICK)
        }

        binding.btnPromoCode.setOnClickListener {
            viewModel.setOnClick(ClickEvents.RETAIL_START_PROMOCODE_BOTTOM_BUTTON_CLICK)
        }

    }

}