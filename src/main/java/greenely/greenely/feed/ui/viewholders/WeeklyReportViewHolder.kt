package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import greenely.greenely.feed.models.feeditems.WeeklyReport
import greenely.greenely.feed.ui.weeklyreport.WeeklyReportView
import vn.tiki.noadapter2.AbsViewHolder

internal class WeeklyReportViewHolder(
        val view: WeeklyReportView
) : AbsViewHolder(view) {
    override fun bind(item: Any?) {
        super.bind(item)
        if (item is WeeklyReport) {
            view.weeklyReport = item
        }
    }
}
