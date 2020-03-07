package greenely.greenely.retailonboarding.steps

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.PriceSummaryFragmentBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.retailonboarding.ui.util.PriceSummaryFlow
import greenely.greenely.retailonboarding.ui.util.RedeemCodeFlow
import greenely.greenely.retailinvite.models.PriceSummaryItem
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.models.RetailPriceSummaryResponse
import greenely.greenely.retailonboarding.ui.PriceSummaryViewModel
import greenely.greenely.retail.ui.events.EventHandler
import greenely.greenely.retailonboarding.ui.events.PriceSummaryErrorHandler
import greenely.greenely.retail.ui.events.RetailInfoEventHandlerFactory
import greenely.greenely.retailinvite.models.StylePref
import greenely.greenely.retailonboarding.ui.viewholders.PriceSummaryViewHolder
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.CommonUtils
import greenely.greenely.utils.getCurrencyFormat
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject

class PriceSummaryFragment : androidx.fragment.app.Fragment(), HasSnackbarView {
    override val snackbarView: View
        get() = binding.mainContainer

    private lateinit var binding: PriceSummaryFragmentBinding

    private lateinit var priceSummaryAdapter: OnlyAdapter


    private lateinit var eventHandler: EventHandler

    @Inject
    lateinit var tracker: Tracker


    private val viewModel: PriceSummaryViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[PriceSummaryViewModel::class.java]
    }

    @Inject
    internal lateinit var eventHandlerFactory: RetailInfoEventHandlerFactory

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var errorHandler: PriceSummaryErrorHandler


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpAdapter()
        eventHandler = eventHandlerFactory.createEventHandler(binding)
        viewModel.events.observe(this, Observer { it?.let {
            eventHandler.handleEvent(it)
        } })



        when (getConfig().flow) {
            is PriceSummaryFlow -> {
                if (viewModel.retailPriceSummary.value == null) viewModel.fetchPriceSummary()
            }
            is RedeemCodeFlow -> {
                if (viewModel.promoCode.value == null || !viewModel.promoCode.value.equals(getConfig().flow?.promoCode))
                    viewModel.promoCode.value = getConfig().flow?.promoCode
            }

        }


        viewModel.retailPriceSummary.observe(this, Observer {
            it?.let {
                setUpPriceSummary(it)
            }
        })

        viewModel.promoCode.observe(this, Observer {
            it?.let {
                viewModel.fetchPriceSummary()
            }
        })


        viewModel.errors.observe(this, Observer {
            it?.let {
                errorHandler.handleError(it)
                binding.mainContainer.visibility = View.INVISIBLE
            }
        })


        val retailOnboardConfig = getConfig()

        binding.btnRedeemCode.setOnClickListener {

            trackRetailEvent("Retail Price Summary", "clicked_have_referral_code")
            when (retailOnboardConfig.flow) {
                is RedeemCodeFlow -> {
                    activity?.onBackPressed()
                }
                is PriceSummaryFlow -> {
                    var fragment = RetailRedeemCodeFragment()
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

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        binding.btnContinue.setOnClickListener {

            var fragment = AddressStepFragment()
            fragment.arguments = Bundle()
            fragment.arguments?.putSerializable(RetailOnboardConfig.KEY_CONFIG, retailOnboardConfig.apply { flow?.promoCode = viewModel.promoCode.value })

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

        setNavigationBarIcon()

    }

    fun setNavigationBarIcon() {

        when (getConfig().flow) {
            is PriceSummaryFlow -> {
                binding.toolbar.setNavigationIcon(R.drawable.ic_close_white)
            }
            is RedeemCodeFlow -> {
                binding.toolbar.setNavigationIcon(R.drawable.arrow_back)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("Retail Price Summary")
    }


    fun getConfig() = arguments?.getSerializable(RetailOnboardConfig.KEY_CONFIG) as RetailOnboardConfig


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)

    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.price_summary_fragment, container, false)
        return binding.root
    }


    private fun setUpPriceSummary(response: RetailPriceSummaryResponse) {


        var priceSummaryItems: MutableList<PriceSummaryItem> = mutableListOf()

        priceSummaryItems.add(PriceSummaryItem(getString(R.string.retail_contract_type_label), response.contractType!!))

        priceSummaryItems.addAll(response.additions.map { p -> p.apply { stylePref = StylePref() } })


        priceSummaryItems.add(PriceSummaryItem(getString(R.string.per_month_fee_label), CommonUtils.getCurrencyFormat(response.monthlyFee) + " kr").apply {

            if (response.rebateEnabled) {
                stylePref = StylePref(false, true, R.color.grey3)
            }

        })

        binding.discountForMonthsLabel.visibility = View.GONE

        if (response.rebateEnabled) {
            priceSummaryItems.add(PriceSummaryItem(getString(R.string.offer_label_price_summary), getString(R.string.discount_for_months_label).format(response.rebateMonthsCounts)).apply {
                stylePref = StylePref(false, false, R.color.black_2, true)
            })

            binding.discountForMonthsLabel.setText(getString(R.string.discount_for_months_price_summary).format(response.rebateMonthsCounts, response.monthlyFee.getCurrencyFormat()))
            binding.discountForMonthsLabel.visibility = View.VISIBLE

        }



        if (response.discount > 0)
            priceSummaryItems.add(PriceSummaryItem(getString(R.string.retail_discount_label), "-" + CommonUtils.getCurrencyFormat(response.discount) + " kr").apply {
                stylePref = StylePref(true, false, highLightColorId = R.color.green_1)
            })



        priceSummaryAdapter.setItems(priceSummaryItems)


        if (response.promocode != null)
            binding.btnRedeemCode.visibility = View.INVISIBLE
        else
            binding.btnRedeemCode.visibility = View.VISIBLE

    }


    @SuppressLint("WrongConstant")
    private fun setUpAdapter() {
        priceSummaryAdapter = OnlyAdapter.Builder()
                .viewHolderFactory { parent, _ -> PriceSummaryViewHolder.create(parent) }
                .build()

        binding.listAttributes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        binding.listAttributes.isNestedScrollingEnabled = true

        binding.listAttributes.adapter = priceSummaryAdapter
    }

    private fun trackRetailEvent(label: String, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Retail",
                label

        ))
    }


}