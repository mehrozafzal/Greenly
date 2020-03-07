package greenely.greenely.guidance.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceFragmentBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.guidance.errors.GuidanceErrorHandler
import greenely.greenely.guidance.models.Article
import greenely.greenely.guidance.models.Offer
import greenely.greenely.guidance.models.Tips
import greenely.greenely.guidance.ui.latestsolaranalysis.LatestSolarAnalysisActivity
import greenely.greenely.guidance.ui.viewholders.GuidanceArticlesViewHolder
import greenely.greenely.guidance.ui.viewholders.GuidanceOffersViewHolder
import greenely.greenely.guidance.ui.viewholders.GuidanceTipsViewHolder
import greenely.greenely.solaranalysis.ui.householdinfo.SolarAnalysisActivity
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import vn.tiki.noadapter2.OnlyAdapter
import javax.inject.Inject


class GuidanceFragment : androidx.fragment.app.Fragment(), HasSnackbarView {
    override val snackbarView: View
        get() = binding.coordinatorLayout

    @Inject
    lateinit var errorHandler: GuidanceErrorHandler

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    private lateinit var viewModel: GuidanceViewModel
    private lateinit var binding: GuidanceFragmentBinding
    private lateinit var offersAdapter: OnlyAdapter
    private lateinit var articlesAdapter: OnlyAdapter
    private lateinit var energyTipsAdapter: OnlyAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.guidance_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[GuidanceViewModel::class.java]

        setUpRecyclerViews()

        viewModel.errors.observe(this, Observer {
            it?.let {
                errorHandler.handleError(it)
            }
        })
        viewModel.guidanceContent.observe(this, Observer {
            it?.let {
                articlesAdapter.setItems(it.articles)
                offersAdapter.setItems(it.offers)
                energyTipsAdapter.setItems(it.tips)
            }
        })


        viewModel.guidanceContent.observe(this, Observer {
            it?.let {
                if (it.isSolarAnalysisEnabled) {
                    binding.guidanceSolarAnalysisTitle.visibility = View.VISIBLE
                    binding.guidanceSolarAnalysisContainer.visibility = View.VISIBLE
                    if (it.latestSolarAnalysis != null) {
                        binding.guidanceCreatedDate.text = it.latestSolarAnalysis.createdDate
                        binding.guidanceLatestAnalysisContainer.visibility = View.VISIBLE
                    } else {
                        binding.guidanceLatestAnalysisContainer.visibility = View.GONE
                    }
                } else {
                    binding.guidanceSolarAnalysisTitle.visibility = View.GONE
                    binding.guidanceSolarAnalysisContainer.visibility = View.GONE
                }

            }
        })

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.guidancePreviousResults.setOnClickListener {
            goToLatestAnalysis()
        }

        handleClickListeners()
    }

    private fun handleClickListeners() {
        binding.guidanceSolarAnalysisBtn.setOnClickListener {
            startActivity(Intent(context, SolarAnalysisActivity::class.java))
        }
    }

    private fun setUpRecyclerViews() {
        setUpAdapters()

        binding.articlesRecyclerView.adapter = articlesAdapter
        binding.articlesRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        binding.articlesRecyclerView.isNestedScrollingEnabled = true


        binding.offeringsRecyclerView.adapter = offersAdapter
        binding.offeringsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        binding.offeringsRecyclerView.isNestedScrollingEnabled = true

        binding.energyTipsRecyclerView.adapter = energyTipsAdapter
        binding.energyTipsRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3, androidx.recyclerview.widget.GridLayoutManager.HORIZONTAL, false)
        binding.energyTipsRecyclerView.isNestedScrollingEnabled = true
    }

    private fun setUpAdapters() {
        offersAdapter = OnlyAdapter.Builder()
                .viewHolderFactory { parent, _ -> GuidanceOffersViewHolder.create(parent) }
                .onItemClickListener { _: View, item: Any, _: Int ->
                    startActivity(Intent(context, OfferDetailActivity::class.java).apply {
                        if (item is Offer) putExtra("offer", item)
                    })
                }
                .build()

        articlesAdapter = OnlyAdapter.Builder()
                .viewHolderFactory { parent, _ -> GuidanceArticlesViewHolder.create(parent) }
                .onItemClickListener { _: View, item: Any, _: Int ->

                    startActivity(Intent(context, ArticleDetailActivity::class.java).apply {
                        if (item is Article) putExtra("article", item)
                    })
                }
                .build()

        energyTipsAdapter = OnlyAdapter.builder()
                .viewHolderFactory { parent, _ -> GuidanceTipsViewHolder.create(parent) }
                .onItemClickListener { _: View, item: Any, _: Int ->
                    startActivity(Intent(context, TipsDetailActivity::class.java).apply {
                        if (item is Tips) putExtra("tip", item)
                    })
                }
                .build()
    }

    private fun startSolarAnalysis() {
        trackSolarAnalysis()
        startActivity(Intent(context, SolarAnalysisActivity::class.java))
    }

    private fun goToLatestAnalysis() {
        tracker.track(TrackerFactory().trackingEvent("sa_previous_result", getString(R.string.solar_analysis_tracking_category)))
        startActivity(Intent(context, LatestSolarAnalysisActivity::class.java))
    }

    private fun trackSolarAnalysis() {
        tracker.track(TrackerFactory().trackingEvent("sa_started", getString(R.string.solar_analysis_tracking_category)))
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchGuidanceContent()
        tracker.trackScreen("Guidance")
    }
}
