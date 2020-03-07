package greenely.greenely.feed.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.text.method.LinkMovementMethod
import greenely.greenely.databinding.FeedNewsItemBinding
import vn.tiki.noadapter2.AbsViewHolder

internal class NewsViewHolder(
        val binding: FeedNewsItemBinding
) : AbsViewHolder(binding.root) {
    init {
        binding.body.movementMethod = LinkMovementMethod.getInstance()
    }
}
