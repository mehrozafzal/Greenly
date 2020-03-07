package greenely.greenely.retail.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nex3z.notificationbadge.NotificationBadge
import greenely.greenely.R
import greenely.greenely.databinding.RetailStartFragmentBinding
import greenely.greenely.retail.ui.events.ClickEvents
import greenely.greenely.utils.initialize
import io.intercom.android.sdk.Intercom
import javax.inject.Inject

class RetailStartFragment : BaseFragment() {

    private lateinit var binding: RetailStartFragmentBinding


    private lateinit var viewModel: RetailViewModel

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_start_fragment, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, factory)[RetailViewModel::class.java]


        binding.btnContinue.setOnClickListener {
            viewModel.setOnClick(ClickEvents.RETAIL_START_TOP_CONTINUE_CLICK)
        }

        binding.btnPromoCode.setOnClickListener {
            viewModel.setOnClick(ClickEvents.RETAIL_START_PROMOCODE_TOP_BUTTON_CLICK)
        }


        prepareNotficationBadge()

    }

    private fun prepareNotficationBadge() {
        val notificationBadge = binding.notificationIcon.badge
        notificationBadge.initialize(binding.notificationIcon.layout)
    }


}