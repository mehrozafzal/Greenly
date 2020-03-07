package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import greenely.greenely.databinding.FeedDateLabelItemBinding
import greenely.greenely.feed.models.feeditems.DateLabel
import vn.tiki.noadapter2.AbsViewHolder

internal class DateLabelViewHolder(
        val binding: FeedDateLabelItemBinding
) : AbsViewHolder(binding.root) {
    override fun bind(item: Any?) {
        super.bind(item)
        if (item is DateLabel) {
            binding.label.text = item.label
        }
    }
}
