package greenely.greenely.utils.pdfview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.jidogoon.pdfrendererview.PdfRendererView
import greenely.greenely.R
import kotlinx.android.synthetic.main.activity_pdf_renderer_view.*

class PdfRendererView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.extras.get("url").toString()

        setContentView(R.layout.activity_pdf_renderer_view)

        pdfRendererView.apply {
            initWithUrl(url)
        }

        pdfRendererView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onDownloadStart() {
                pdfRendererProgressBar.visibility = View.VISIBLE
            }

            override fun onDownloadSuccess() {
                pdfRendererProgressBar.visibility = View.GONE
            }

            override fun onError(error: Throwable) {
                pdfRendererProgressBar.visibility = View.GONE
            }
        }
    }
}