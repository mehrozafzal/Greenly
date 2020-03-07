package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.text.method.LinkMovementMethod
import greenely.greenely.databinding.FeedTipItemBinding
import vn.tiki.noadapter2.AbsViewHolder

internal class TipViewHolder(
        val binding: FeedTipItemBinding
) : AbsViewHolder(binding.root) {
    init {
        binding.body.movementMethod = LinkMovementMethod.getInstance()
    }
}
