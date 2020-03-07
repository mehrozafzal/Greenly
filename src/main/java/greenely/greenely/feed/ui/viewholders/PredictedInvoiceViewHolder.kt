package greenely.greenely.feed.ui.viewholders

import greenely.greenely.databinding.PredictedInvoiceFeedItemBinding
import greenely.greenely.feed.models.feeditems.PredictedInvoiceItem
import vn.tiki.noadapter2.AbsViewHolder

class PredictedInvoiceViewHolder(val binding: PredictedInvoiceFeedItemBinding) : AbsViewHolder(binding.root) {

    override fun bind(item: Any?) {
        super.bind(item)
        if (item is PredictedInvoiceItem) {
            binding.model=item
        }
    }
}