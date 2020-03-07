package greenely.greenely.retailonboarding.ui.viewholders

import androidx.databinding.DataBindingUtil
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import dagger.multibindings.ElementsIntoSet
import greenely.greenely.R
import greenely.greenely.databinding.PriceSummaryListItemBinding
import greenely.greenely.retailinvite.models.PriceSummaryItem
import greenely.greenely.utils.setCustomTextStyle
import greenely.greenely.utils.strikeThroughText
import vn.tiki.noadapter2.AbsViewHolder

class PriceSummaryViewHolder(
        private val binding: PriceSummaryListItemBinding
) : AbsViewHolder(binding.root) {
    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): AbsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: PriceSummaryListItemBinding = DataBindingUtil.inflate(
                    inflater, R.layout.price_summary_list_item, parent, false
            )
            return PriceSummaryViewHolder(binding)
        }
    }

    override fun bind(item: Any?) {
        super.bind(item)
        if (item is PriceSummaryItem) {
            binding.priceItem = item


            binding.value.apply {

                if(item.stylePref.isStyleApplicable())
                {
                    if (item.stylePref.highLightValue) {
                        this.setCustomTextStyle(R.style.TextAppearance_PriceSummaryHighlight)
                        setTextColor(ContextCompat.getColor(context, item.stylePref.highLightColorId))
                    }
                    if (item.stylePref.isBold) {
                        this.setCustomTextStyle(R.style.TextAppearance_BoldPriceSummary)
                    }

                    if (item.stylePref.isStikeThrough)
                        this.strikeThroughText()
                    setTextColor(ContextCompat.getColor(context, item.stylePref.highLightColorId))


                }
                else {
                    this.setCustomTextStyle(R.style.TextAppearance_DefaultPriceSummary)
                }
            }


        }

    }

}