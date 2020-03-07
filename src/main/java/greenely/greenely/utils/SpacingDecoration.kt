package greenely.greenely.utils

import android.graphics.Rect
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView
import android.view.View

/**
 * Decorator for adding spacing to recycler view elements.
 */
class SpacingDecoration(
        @Dimension(unit = Dimension.DP) private val spacing: Int
) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        val position = view.let { parent.getChildAdapterPosition(it) }
        val maxPosition = parent.adapter?.itemCount?.minus(1) ?: return
        outRect.top = spacing
        if (position == maxPosition) {
            outRect.bottom = spacing
        }
    }
}

