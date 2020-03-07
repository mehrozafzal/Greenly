package greenely.greenely.history.views

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
import greenely.greenely.databinding.HistoryChartListFragmentBinding
import greenely.greenely.history.HistoryComponent
import greenely.greenely.history.HistoryViewModel
import greenely.greenely.history.models.HeaderModel
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Fragment for displaying as list of charts in the history view.
 */
class ChartListFragment : androidx.fragment.app.Fragment() {

    companion object {
        /**
         * Create a chart list fragment for a specified [date].
         */
        fun create(date: DateTime,headerModel: HeaderModel): ChartListFragment =
                ChartListFragment().apply {
                    this.date = date
                    this.headerModel=headerModel
                }
    }

    @Inject
    internal lateinit var factory: ViewModelProvider.Factory

    private lateinit var viewModel: HistoryViewModel

    private lateinit var date: DateTime

    private lateinit var headerModel: HeaderModel

    private lateinit var binding: HistoryChartListFragmentBinding

    /**
     * Create the view and the binding.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.history_chart_list_fragment, container, false)

        return binding.root
    }

    /**
     * Create and bind the view model
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!, factory)[HistoryViewModel::class.java]
        viewModel.getComponents().observe(this, Observer {
            it?.let { setUpComponentList(it) }
        })
    }

    private fun setUpComponentList(components: List<HistoryComponent>) {
        components.forEach {
            val transaction = childFragmentManager
                    .beginTransaction()
            try {
                if (childFragmentManager.findFragmentByTag(it.name) == null) {
                    when (it) {
                        HistoryComponent.USAGE -> {
                            transaction.add(R.id.chart_list, ChartHolderFragment.create(date,headerModel), it.name)
                        }
                        HistoryComponent.DISTRIBUTION -> {
                            transaction.add(R.id.chart_list, DistributionFragment.create(date), it.name)
                        }
                    }
                }
            } finally {
                transaction.commit()
            }
        }
    }

    /**
     * Inject whan attaching to [context].
     */
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

