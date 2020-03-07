package greenely.greenely.utils

import androidx.core.widget.ContentLoadingProgressBar
import android.view.View
import greenely.greenely.OpenClassOnDebug
import javax.inject.Inject

/**
 * Handler for loading.
 */
interface LoadingHandler {
    /**
     * If the view is currently loading.
     */
    var loading: Boolean
}

/**
 * Handles loading when the loader overlaps other content.
 */
class OverlappingLoaderHandler(
        private val loader: ContentLoadingProgressBar,
        private val content: View
) : LoadingHandler {

    private var lastVisibility = loader.visibility

    init {
        loader.viewTreeObserver.addOnGlobalLayoutListener {
            if (loader.visibility != lastVisibility) {
                if (loader.visibility == View.VISIBLE) content.visibility = View.INVISIBLE
                else content.visibility = View.VISIBLE

                lastVisibility = loader.visibility
            }
        }
    }

    override var loading: Boolean = false
        set(value) {
            if (value) loader.show()
            else {
                loader.hide()
                if (loader.visibility == View.GONE) {
                    content.visibility = View.VISIBLE
                }
            }
        }
}

/**
 * Factory for [OverlappingLoaderHandler] mostly just a proxy in order to mock the loader.
 */
@OpenClassOnDebug
class OverlappingLoaderFactory @Inject constructor() {
    /**
     * Create a registration loading.
     */
    fun createLoadingHandler(loader: ContentLoadingProgressBar, content: View): LoadingHandler {
        return OverlappingLoaderHandler(loader, content)
    }
}
