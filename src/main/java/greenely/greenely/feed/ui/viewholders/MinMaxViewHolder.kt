package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.text.method.LinkMovementMethod
import greenely.greenely.databinding.FeedMinMaxItemBinding
import greenely.greenely.feed.models.feeditems.MinMax
import vn.tiki.noadapter2.AbsViewHolder


internal class MinMaxViewHolder(
        val binding: FeedMinMaxItemBinding
) : AbsViewHolder(binding.root) {
    init {
        binding.body.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun bind(item: Any?) {
        super.bind(item)
        if (item is MinMax) {
            binding.minMax = item
        }
    }
}
