package greenely.greenely.utils.pdfview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import java.net.URLEncoder

class PdfWebView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.extras.get("url").toString()

        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            loadUrl("https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(url, "utf-8"))
        }
        setContentView(webView)
    }
}
