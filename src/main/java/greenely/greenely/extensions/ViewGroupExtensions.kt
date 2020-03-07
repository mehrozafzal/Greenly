package greenely.greenely.extensions

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import greenely.greenely.R

/**
 * Tag view for when a view is a separator.
 */
class SeparatorView(context: Context) : View(context)

/**
 * Helper for iterating over all the child views in the [ViewGroup]
 *
 * @param callback The function to be called for each child [View]
 */
inline fun ViewGroup.forEach(callback: (View) -> Unit) {
    (0..childCount - 1).forEach {
        callback(getChildAt(it))
    }
}

/**
 * Indexed version of [forEach].
 *
 * @param callback The function to be called for each child [View]
 */
inline fun ViewGroup.forEachIndexed(callback: (Int, View) -> Unit) {
    (0..childCount - 1).forEach {
        callback(it, getChildAt(it))
    }
}

/**
 * Helper for removing all the [SeparatorView] from a [ViewGroup].
 */
fun ViewGroup.removeAllSeparators() {
    val separators = mutableListOf<Int>()
    forEachIndexed { i, view -> if (view is SeparatorView) separators += i }
    separators.forEachIndexed { index, i -> removeViewAt(i - index) }
}

/**
 * Create a new [SeparatorView].
 */
fun ViewGroup.createSeparator(): View = SeparatorView(context).apply {
    layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            context.dp(1).toInt()
    )
    background = ColorDrawable(ContextCompat.getColor(context, R.color.divider_color))
}

/**
 * Helper for adding [SeparatorView] in a vertically oriented view.
 */
fun ViewGroup.addVerticalSeparators() {
    removeAllSeparators()
    print(childCount)

    if (childCount > 0) {
        val separators = mutableListOf<Pair<Int, View>>()
        (1..childCount - 1).forEach {
            separators.add(
                    it + separators.size to createSeparator()
            )
        }
        separators.forEach { addView(it.second, it.first) }
    }
}

