package greenely.greenely.guidance.ui.viewholders

import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceTipItemBinding
import greenely.greenely.extensions.dp
import greenely.greenely.guidance.models.Tips
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import vn.tiki.noadapter2.AbsViewHolder

class GuidanceTipsViewHolder(
        private val binding: GuidanceTipItemBinding
) : AbsViewHolder(binding.root) {
    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): AbsViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: GuidanceTipItemBinding = DataBindingUtil.inflate(
                    inflater, R.layout.guidance_tip_item, parent, false
            )
            return GuidanceTipsViewHolder(binding)
        }
    }

    private val screenWidth: Int
        get() = binding.root.context.resources.displayMetrics.widthPixels

    override fun bind(item: Any?) {
        super.bind(item)
        itemView.apply {
            layoutParams = LinearLayout.LayoutParams(
                    (screenWidth - context.dp(36)).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        if (item is Tips) {
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
                .apply(RequestOptions.placeholderOf(R.drawable.place_holder))
                .apply(RequestOptions.bitmapTransform(multiTransformation))
                .into(binding.image)
    }

}

