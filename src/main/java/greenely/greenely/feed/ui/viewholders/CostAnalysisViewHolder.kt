package greenely.greenely.feed.ui.viewholders

import greenely.greenely.databinding.FeedCostAnalysisItemBinding
import greenely.greenely.feed.models.feeditems.CostAnalysisFeedItem
import greenely.greenely.feed.models.feeditems.MinMax
import greenely.greenely.feed.ui.monthlyreport.FeedMonthlyReportView
import greenely.greenely.feed.utils.charting.CostAnalysisChartManager
import greenely.greenely.feed.utils.charting.MonthlyReportChartManager
import vn.tiki.noadapter2.AbsViewHolder
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import greenely.greenely.tracking.Tracker
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.getMonthAndYear
import org.joda.time.format.DateTimeFormat


class CostAnalysisViewHolder(val binding :FeedCostAnalysisItemBinding,var tracker: Tracker) : AbsViewHolder(binding.root)  {


    override fun bind(item: Any?) {
        super.bind(item)
        if (item is CostAnalysisFeedItem) {
            binding.model = item
            CostAnalysisChartManager(binding.chart, FeedMonthlyReportView.dateFormatter).chartData = item.chartData
            item.chartData?.let {
                trackFeedEvent(it.date.getMonthAndYear(),"seen_costanalysis" )
            }

        }
    }

    private fun trackFeedEvent(label: String, name: String) {
        tracker.track(TrackerFactory().trackingEvent(
                name,
                "Feed",
                label

        ))
    }


}

