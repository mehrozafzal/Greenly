package greenely.greenely.retailonboarding.steps

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
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.RetailRedeemCodeFragmentBinding
import greenely.greenely.extensions.hideKeyboard
import greenely.greenely.models.Resource
import greenely.greenely.retailonboarding.ui.util.PriceSummaryFlow
import greenely.greenely.retailonboarding.ui.util.RedeemCodeFlow
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.ui.PriceSummaryViewModel
import greenely.greenely.retail.ui.RedeemCodeErrorHandler
import greenely.greenely.retailonboarding.ui.RedeemCodeViewModel
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.CommonUtils
import javax.inject.Inject

class RetailRedeemCodeFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: RetailRedeemCodeFragmentBinding

    @Inject
    lateinit var errorHandler: RedeemCodeErrorHandler


    private val viewModel: RedeemCodeViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)[RedeemCodeViewModel::class.java]
    }

    private val priceSummaryViewModel: PriceSummaryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[PriceSummaryViewModel::class.java]
    }

    @Inject
    lateinit var tracker: Tracker

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory


    private val retailOnboardConfig: RetailOnboardConfig by lazy {
        arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.retail_redeem_code_fragment, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        setDescription()


        binding.toolbar.setNavigationOnClickListener {
            activity?.hideKeyboard()
            activity?.onBackPressed()
        }



        viewModel.verifyPromoCodeResponse.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    hideLoader()
                    if (it.value.isValid) {
                        priceSummaryViewModel.promoCode.value = it.value.promocode
                        proceed()
                    }
                }
                is Resource.Loading -> showLoader()
                is Resource.Error -> {
                    hideLoader()
                    errorHandler.handleError(it.error)
                }
            }
        })



        binding.edtRedeemCode.setOnEditorActionListener(TextView.OnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                viewModel.verifyPromoCode()
                return@OnEditorActionListener true
            }
            false
        })

        binding.btnRedeemCode.setOnClickListener {
            trackEvent("Referral Redeem Code","clicked_register_referral_code")
            viewModel.verifyPromoCode()
        }

        setNavigationBarIcon()

    }

    private fun trackEvent(label: String,name:String){
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Referral",
                label

        ))
    }



    fun setNavigationBarIcon() {

        when (retailOnboardConfig.flow) {
            is PriceSummaryFlow -> {
                binding.toolbar.setNavigationIcon(R.drawable.arrow_back_green)
            }

            is RedeemCodeFlow -> {
                binding.toolbar.setNavigationIcon(R.drawable.ic_close_green)

            }
        }


    }


    fun proceed() {

        var flow = retailOnboardConfig.flow
        activity?.hideKeyboard()
        when (flow) {
            is PriceSummaryFlow -> {
                activity?.onBackPressed()
            }

            is RedeemCodeFlow -> {

                flow.promoCode = binding.edtRedeemCode.text.toString()
                retailOnboardConfig.flow = flow

                var fragment = PriceSummaryFragment()
                fragment.arguments = Bundle()
                fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig)
                fragmentManager
                        ?.beginTransaction()
                        ?.addToBackStack(this.javaClass.simpleName)
                        ?.setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right)
                        ?.replace(R.id.content, fragment)
                        ?.commit()


            }
        }
    }


    private fun showLoader() {
        binding.loader.visibility = View.VISIBLE
        binding.btnRedeemCode.visibility = View.INVISIBLE
    }

    private fun hideLoader() {
        binding.loader.visibility = View.INVISIBLE
        binding.btnRedeemCode.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Referral Redeem Code")
    }


    private fun setDescription() {
        binding.redeemDescription.setText(String.format(getString(R.string.redeem_code_description), CommonUtils.getCurrencyFormat(retailOnboardConfig.discount)))

    }

}