package greenely.greenely.retail.ui.viewholders

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import greenely.greenely.R
import greenely.greenely.databinding.RetailInvoiceItemBinding
import greenely.greenely.retail.models.Invoice
import vn.tiki.noadapter2.AbsViewHolder

class RetailInvoicesViewHolder(
        private val binding: RetailInvoiceItemBinding
) : AbsViewHolder(binding.root) {
    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): AbsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: RetailInvoiceItemBinding = DataBindingUtil.inflate(
                    inflater, R.layout.retail_invoice_item, parent, false
            )
            return RetailInvoicesViewHolder(binding)
        }
    }

    override fun bind(item: Any?) {
        super.bind(item)

        if (item is Invoice) {
            binding.title.text = item.month
            binding.state.text = item.state
            binding.cost.text = (item.cost.toInt() / 100).toString() + "kr"
            if (item.isPaid)
                binding.image.setImageResource(R.drawable.green_tick)
            else
                binding.image.setImageResource(R.drawable.invoice_not_paid)
        }

    }

}

