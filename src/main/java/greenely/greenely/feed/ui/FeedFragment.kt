package greenely.greenely.feed.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.*
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.extensions.dp
import greenely.greenely.feed.models.feeditems.*
import greenely.greenely.feed.ui.monthlyreport.FeedMonthlyReportView
import greenely.greenely.feed.ui.viewholders.*
import greenely.greenely.feed.ui.weeklyreport.WeeklyReportView
import greenely.greenely.feed.utils.errors.FeedErrorHandler
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.router.Route
import greenely.greenely.models.Resource
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.LoadingHandler
import greenely.greenely.utils.OverlappingLoaderFactory
import greenely.greenely.utils.SpacingDecoration
import greenely.greenely.utils.getMonthAndYear
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject

/**
 * Fragment for showing the new feedItems.
 */
class FeedFragment : androidx.fragment.app.Fragment(), HasSnackbarView {
    override val snackbarView: View
        get() = binding.feedRoot

    @Inject
    lateinit var errorHandler: FeedErrorHandler

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var loadingFactory: OverlappingLoaderFactory

    private  val adapter: OnlyAdapter by lazy {
        setUpAdapter()
    }

    private lateinit var binding: FeedFragmentBinding

    private lateinit var viewModel: FeedViewModel

    private lateinit var loader: LoadingHandler

    @Inject
    lateinit var tracker: Tracker

    val activityCallBack:IMainAcitvityListener by lazy {
        activity as IMainAcitvityListener
    }

   val  historyViewModel : HistoryViewModel by lazy {
       ViewModelProviders.of(activity!!, factory)[HistoryViewModel::class.java]
   }


    /**
     * Create the view.
     */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tracker.trackScreen("Feed")
    }

    /**
     * Inject when attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }



    /**
     * Create and bind to the view model.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loader = loadingFactory.createLoadingHandler(binding.progress, binding.feedRoot)
        viewModel = ViewModelProviders.of(activity!!, factory)[FeedViewModel::class.java]

        viewModel.feed.observe(this, Observer {
            it?.let {
                when (it) {
                    is Resource.Loading -> {
                        loader.loading = true
                    }
                    is Resource.Error -> {
                        clearAllLoaders()
                        Log.e("Blargh", "Error occured", it.error)
                        errorHandler.handleError(it.error)
                    }
                    is Resource.Success -> {
                        clearAllLoaders()
                        adapter.setItems(it.value)
                    }
                }
            }
        })

        binding.viewModel = viewModel
        binding.feed.adapter = adapter
        binding.feed.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        val dividerDecoration = SpacingDecoration(activity!!.dp(8).toInt())
        binding.feed.addItemDecoration(dividerDecoration)

//        getFeedBackground()?.let {
//            binding.feed.background = it
//        }
    }

    private fun setUpAdapter() :OnlyAdapter {
        val layoutInflater = LayoutInflater.from(context)
       return OnlyAdapter.Builder()
                .typeFactory {
                    when (it) {
                        is DateLabel -> R.layout.feed_date_label_item
                        is Message -> R.layout.feed_message_item
                        is MinMax -> R.layout.feed_min_max_item
                        is MonthlyReport -> R.layout.feed_month_item
                        is News -> R.layout.feed_news_item
                        is NoMoreItems -> R.layout.feed_no_more_items_item
                        is Tip -> R.layout.feed_tip_item
                        is WeeklyReport -> R.layout.feed_weekly_item
                        is CostAnalysisFeedItem -> R.layout.feed_cost_analysis_item
                        is PredictedYearlySavingsFeedItem -> R.layout.predicted_savings_yearly_feed_item
                        is PredictedInvoiceItem -> R.layout.predicted_invoice_feed_item

                        else -> throw IllegalArgumentException("Unsupported feed item type")
                    }
                }
                .viewHolderFactory { parent, type ->
                    when (type) {
                        R.layout.feed_date_label_item -> DateLabelViewHolder(FeedDateLabelItemBinding.inflate(layoutInflater, parent, false))
                        R.layout.feed_message_item -> MessageViewHolder(FeedMessageItemBinding.inflate(layoutInflater, parent, false))
                        R.layout.feed_min_max_item -> MinMaxViewHolder(FeedMinMaxItemBinding.inflate(layoutInflater, parent, false))
                        R.layout.feed_month_item -> MonthlyReportViewHolder(FeedMonthlyReportView(context!!).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

                        }).apply {
                            view.setOnClickListener {

                                view.monthlyReport?.let {
                                    var sum=it.chartData?.sum

                                    it.chartData?.let {
                                        historyViewModel.setSelectionFromFeed(sum,it.get(0).date.millis)
                                        trackFeedEvent( it.date.getMonthAndYear(),"clicked_monthly_report")
                                    }

                                    navigateToHistoryScreen()
                                }
                            }
                        }
                        R.layout.feed_news_item -> NewsViewHolder(FeedNewsItemBinding.inflate(layoutInflater, parent, false))
                        R.layout.feed_no_more_items_item -> NoMoreItemsViewHolder(layoutInflater.inflate(R.layout.feed_no_more_items_item, parent, false))
                        R.layout.feed_tip_item -> TipViewHolder(FeedTipItemBinding.inflate(layoutInflater, parent, false))
                        R.layout.feed_weekly_item -> WeeklyReportViewHolder(WeeklyReportView(context!!).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        })
                        R.layout.feed_cost_analysis_item -> CostAnalysisViewHolder(FeedCostAnalysisItemBinding.inflate(layoutInflater, parent, false).apply {


                            info.setOnClickListener {
                                model?.let {
                                    it.chartData?.let {
                                        trackFeedEvent(it.date.getMonthAndYear(),"clicked_costanalysis_tooltip")
                                    }

                                }
                                navigateToCostAnalysisInfoActivity()
                            }

                            btnCostAnalysis.setOnClickListener {
                                model?.let {
                                    it.chartData?.let {
                                        trackFeedEvent(it.date.getMonthAndYear(),"clicked_costanalysis_retail")
                                    }

                                }
                                navigateToRetailScreen()

                            }


                        },tracker)
                        R.layout.predicted_savings_yearly_feed_item -> PredictedYearlySavingViewHolder(PredictedSavingsYearlyFeedItemBinding.inflate(layoutInflater,parent,false).apply {
                            lockedIcon.setOnClickListener {
                                trackFeedEvent(null,"clicked_predicted_bill_unlock")
                                navigateToRetailScreen()
                            }

                        })
                        R.layout.predicted_invoice_feed_item -> PredictedInvoiceViewHolder(PredictedInvoiceFeedItemBinding.inflate(layoutInflater,parent,false))


                        else -> throw IllegalArgumentException("Could not create view holder.")
                    }
                }
                .build()
    }


    private fun getFeedBackground(): Drawable? =
            VectorDrawableCompat.create(resources, R.drawable.feed_background, null)

    private fun clearAllLoaders() {
        binding.refresh.isRefreshing = false
        loader.loading = false
    }


    private fun navigateToCostAnalysisInfoActivity() {
        startActivity(Intent(context,CostAnalysisInfoActivity::class.java))
    }

    private fun navigateToRetailScreen() {
       activityCallBack.routeTo(Route.RETAIL)
    }

    private fun navigateToHistoryScreen() {
        activityCallBack.routeTo(Route.HISTORY)
    }

    private fun trackFeedEvent(label: String?, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Feed",
                label

        ))
    }
}
