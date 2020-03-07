package greenely.greenely.utils.pdfview

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import greenely.greenely.R
import greenely.greenely.databinding.PdfViewerBinding


class PdfViewer : AppCompatActivity() {
    lateinit var binding: PdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.pdf_viewer)

        try {
            val fileName = intent.extras.get("fileName").toString()
            binding.pdfview.fromAsset(fileName)
                    .load()
        } catch (e: Exception) {
            Log.e("TAG", e.message)
        }
    }

}
