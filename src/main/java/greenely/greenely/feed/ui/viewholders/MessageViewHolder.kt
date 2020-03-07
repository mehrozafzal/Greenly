package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.text.method.LinkMovementMethod
import greenely.greenely.databinding.FeedMessageItemBinding
import greenely.greenely.feed.models.feeditems.Message
import vn.tiki.noadapter2.AbsViewHolder


internal class MessageViewHolder(
        val binding: FeedMessageItemBinding
) : AbsViewHolder(binding.root) {
    init {
        binding.description.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun bind(item: Any?) {
        super.bind(item)
        if (item is Message) {
            binding.message = item
        }
    }
}

