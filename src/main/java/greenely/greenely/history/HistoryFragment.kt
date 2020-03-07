package greenely.greenely.history

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import dagger.android.support.AndroidSupportInjection
import greenely.greenely.R
import greenely.greenely.databinding.HistoryFragmentBinding
import greenely.greenely.errors.HasSnackbarView
import greenely.greenely.history.models.HeaderModel
import greenely.greenely.history.views.*
import greenely.greenely.home.util.IMainAcitvityListener
import greenely.greenely.main.router.Route
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.formatDate
import greenely.greenely.utils.onPageSelected
import kotlinx.android.synthetic.main.history_fragment.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject


class HistoryFragment : androidx.fragment.app.Fragment(), HasSnackbarView {

    override val snackbarView: View
        get() = binding.scrollView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tracker: Tracker

    @Inject
    lateinit var errorHandler: HistoryErrorHandler

    private lateinit var binding: HistoryFragmentBinding

    private lateinit var viewModel: HistoryViewModel

    private var shouldUpdatePageIndex: Boolean = true

    private val handler = Handler(Looper.getMainLooper())

    private var resolution: HistoryResolution? = null

    private var isShowingSnackBar = false

    private lateinit var adapter: NavigationViewPagerAdapter

    private var historyResponse: HistoryResponse? = null

    val activityCallBack: IMainAcitvityListener by lazy {
        activity as IMainAcitvityListener
    }

    companion object {
        fun create(resolution: HistoryResolution) =
                HistoryFragment().apply {
                    this.resolution = resolution
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.history_fragment, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)[HistoryViewModel::class.java]
        viewModel.retailStateHandler.refreshRetailState()

        viewModel.getEvents().observe(this, Observer {
            when (it) {
                is UiEvent.ShowMore -> handleShowMore(it.historyResolution)
                is UiEvent.ShowError -> errorHandler.handleError(it.error)
                is UiEvent.ShowRetail -> navigateToRetailScreen()
                is UiEvent.StartActivity -> {
                    val intent = Intent(context, it.activity)
                    if (it.requestCode != null) activity!!.startActivityForResult(intent, it.requestCode)
                    else activity!!.startActivity(intent)
                }
            }
        })

        viewModel.isLoading().observe(this, Observer { it?.let { setShowLoader(it) } })


        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                var position = position
                historyResponse?.let {
                    var data = it.navigationData.get(position).data


                    if (viewModel.resolution.get() == HistoryResolution.Year) {
                        data?.firstOrNull()?.let {
                            period?.text = DateTimeFormat
                                    .forPattern("YYYY")
                                    .withLocale(Locale("sv", "SE"))
                                    .print(it.dateTime)

                        }
                    } else {
                        var startDate = data.first().dateTime.getDateForMonthResolution().formatDate()
                        var lastDate = data.last().dateTime.getDateForMonthResolution().formatDate()
                        period?.text = startDate + " - " + lastDate

                    }

                }

            }
        })


        viewModel.getNavigationPageIndex().observe(this, Observer {
            it?.let { updateNavigationPageIndex(it) }
        })

        viewModel.getSelectedDataPoint().observe(this, Observer {
            it?.let {
                viewModel.resolution.get()?.let { resolution ->
                    var headerModel = HeaderModel.forResolutionWithDataPoint(
                            context!!,
                            resolution,
                            it)
                    updateContent(it, headerModel)
                }

            }
        })

        viewModel.getHistory(resolution ?: HistoryResolution.Year).observe(this, Observer {
            it?.let {
                historyResponse = it
                if (!viewModel.isDataAvailable.get()) {
                    showDataMissingScreen()
                }
                else
                updateNavigationData(it)

            }


        })






        binding.viewModel = viewModel

    }

    private fun updateNavigationData(it: HistoryResponse) {
        adapter = NavigationViewPagerAdapter(childFragmentManager).apply { data = it.navigationData }
        binding.pager.adapter = adapter
        adapter.data = it.navigationData
        if(it.navigationData.size==1)
        {
            binding.pageIndicatorView.setSelected(0)
            binding.pageIndicatorView.count=1

            var position = 0
            historyResponse?.let {
                var data = it.navigationData.get(position).data


                if (viewModel.resolution.get() == HistoryResolution.Year) {
                    data?.firstOrNull()?.let {
                        period?.text = DateTimeFormat
                                .forPattern("YYYY")
                                .withLocale(Locale("sv", "SE"))
                                .print(it.dateTime)

                    }
                } else {
                    var startDate = data.first().dateTime.getDateForMonthResolution().formatDate()
                    var lastDate = data.last().dateTime.getDateForMonthResolution().formatDate()
                    period?.text = startDate + " - " + lastDate

                }

            }


        }

    }


    private fun updateContent(it: NavigationDataPoint, headerModel: HeaderModel): Boolean {
        return handler.post {
            if (isAdded) {
                if (it.hasDetailedData) {
                    childFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.content, ChartListFragment.create(it.dateTime, headerModel))
                            .commitAllowingStateLoss()
                } else {
                    childFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.content, NoContentFragment.create(headerModel))
                            .commitAllowingStateLoss()

                    resetLayoutMargins()
                }
            }
        }
    }

    private fun showDataMissingScreen(): Boolean {
        return handler.post {
            if (isAdded) {
                binding.topChartContainer.visibility=View.GONE
                childFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.content, HistoryNotOperationalStateFragment())
                        .commitAllowingStateLoss()
            }
        }
    }


    private fun updateNavigationPageIndex(it: Int): Boolean {
        shouldUpdatePageIndex = false
        return handler.post {
            var index = adapter.getSelectDataPointIndex(viewModel.getSelectedDataPoint().value)
            if (index > -1) binding.pager.currentItem = index
            else binding.pager.currentItem = it
            binding.topChartContainer.visibility = View.VISIBLE
            shouldUpdatePageIndex = true
        }
    }

    private fun trackScreenToSegment() {
        if(viewModel.resolution.get() is HistoryResolution.Year)
        tracker.trackScreen("History Month")
        else
            tracker.trackScreen("History Day")

    }

    override fun onResume() {
        super.onResume()
        setResolutionListener()
        trackScreenToSegment()

    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    private fun handleShowMore(nextResolution: HistoryResolution) {
        viewModel.fetchHistory()
    }

    fun setResolutionListener() {
        binding.timeResolution.setOnCheckedChangeListener(null)
        binding.timeResolution.check(viewModel.selectedResolution.get())

        binding.timeResolution.setOnCheckedChangeListener { _, checkedId ->
            viewModel.showMore()
            if(checkedId==R.id.years)
                tracker.trackScreen("History Month")
            else
                tracker.trackScreen("History Day")
                trackHistoryEvent(null,"changed_history_resolution")


        }
        binding.timeResolution.check(viewModel.selectedResolution.get())
    }

    private fun setShowLoader(show: Boolean) {
        if (show) {
            binding.progress.show()
        } else {
            if (binding.progress.visibility == View.GONE) {
                binding.progress.hide()
                binding.timeResolution.visibility = View.VISIBLE
            } else {
                binding.progress.hide()
                binding.timeResolution.visibility = View.VISIBLE
            }
        }
    }

    private fun resetLayoutMargins() {

    }

    private fun trackHistoryEvent(label: String?, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "History",
                label

        ))
    }

    private fun navigateToRetailScreen() {
        trackHistoryEvent("history","clicked_retail_offer")
        activityCallBack.routeTo(Route.RETAIL)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        viewModel.history.value=null
//    }


//    override fun onDestroyView() {
//        for (fragment in childFragmentManager.fragments) {
//            childFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
//        }
//        super.onDestroyView()
//    }
}


fun DateTime.getDateForMonthResolution(): String = DateTimeFormat
        .forPattern("d MMM")
        .withLocale(Locale("sv", "SE"))
        .print(this)


