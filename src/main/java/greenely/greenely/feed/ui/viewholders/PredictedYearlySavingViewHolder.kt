package greenely.greenely.feed.ui.viewholders

import greenely.greenely.databinding.PredictedSavingsYearlyFeedItemBinding
import greenely.greenely.feed.models.feeditems.PredictedYearlySavingsFeedItem
import greenely.greenely.tracking.Tracker
import vn.tiki.noadapter2.AbsViewHolder

class PredictedYearlySavingViewHolder(val binding : PredictedSavingsYearlyFeedItemBinding) : AbsViewHolder(binding.root) {

    override fun bind(item: Any?) {
        super.bind(item)
        if(item is PredictedYearlySavingsFeedItem) {
            binding.model=item
        }
    }
}