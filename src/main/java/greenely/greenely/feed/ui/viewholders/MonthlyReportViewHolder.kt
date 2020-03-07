package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import greenely.greenely.feed.models.feeditems.MonthlyReport
import greenely.greenely.feed.ui.monthlyreport.FeedMonthlyReportView
import vn.tiki.noadapter2.AbsViewHolder

internal class MonthlyReportViewHolder(
        val view: FeedMonthlyReportView
) : AbsViewHolder(view) {
    override fun bind(item: Any?) {
        super.bind(item)
        if (item is MonthlyReport) {
            view.monthlyReport = item
        }
    }
}

