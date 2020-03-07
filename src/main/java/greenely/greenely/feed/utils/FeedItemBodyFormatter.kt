package greenely.greenely.feed.utils

import android.os.Build
import android.text.Html
import greenely.greenely.OpenClassOnDebug
import javax.inject.Inject

@OpenClassOnDebug
class FeedItemBodyFormatter @Inject constructor() {
    fun formatBody(body: String): CharSequence {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(body)
        }
    }
}
