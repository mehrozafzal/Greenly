package greenely.greenely.guidance.ui.viewholders


import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.RequestOptions.placeholderOf
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceOfferArticleItemBinding
import greenely.greenely.extensions.dp
import greenely.greenely.guidance.models.Offer
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vn.tiki.noadapter2.AbsViewHolder

class GuidanceOffersViewHolder(
        private val binding: GuidanceOfferArticleItemBinding
) : AbsViewHolder(binding.root) {
    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): AbsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: GuidanceOfferArticleItemBinding = DataBindingUtil.inflate(
                    inflater, R.layout.guidance_offer_article_item, parent, false
            )
            return GuidanceOffersViewHolder(binding)
        }
    }

    private val screenWidth: Int
        get() = binding.root.context.resources.displayMetrics.widthPixels

    override fun bind(item: Any?) {
        super.bind(item)

        itemView.apply {
            layoutParams = LinearLayout.LayoutParams(
                    (screenWidth * 0.6).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }


        if (item is Offer) {
            binding.title.text = item.thumbnailTitle
            binding.label.text = item.label
            binding.image.loadImage(item.thumbnailImageUrl)
        }

    }

    private fun ImageView.loadImage(url: String) {
        val multiTransformation = MultiTransformation(
                CenterCrop(),
                RoundedCornersTransformation(
                        context.dp(16).toInt(), 0, RoundedCornersTransformation.CornerType.ALL
                )
        )

        Glide.with(context)
                .load(url)
                .apply(placeholderOf(R.drawable.place_holder))
                .apply(bitmapTransform(multiTransformation))
                .into(binding.image)
    }


}
