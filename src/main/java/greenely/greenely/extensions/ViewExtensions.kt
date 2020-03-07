package greenely.greenely.extensions

import android.view.View
import android.view.ViewTreeObserver

/**
 * Helper for adding a [callback] when a [View] is finished with the layout.
 *
 * @param callback The function to be called after a [View] is finished with the layout.
 */
inline fun View.onLayouted(crossinline callback: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                /** Called when the layout is finished. */
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback()
                }
            }
    )
}

