package greenely.greenely.feed.ui

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import greenely.greenely.R
import greenely.greenely.databinding.CostAnalysisInfoBinding
import greenely.greenely.utils.underlineText

class CostAnalysisInfoActivity : AppCompatActivity() {

    private lateinit var binding: CostAnalysisInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.cost_analysis_info)

        binding.link.setOnClickListener {
            openUrl("https://intercom.help/greenely/elhandel-och-elavtal/greenelys-elavtal-vanliga-fragor")
        }


//        binding.link.paintFlags = binding.link.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG
        binding.link.underlineText()


        binding.close.setOnClickListener {
            onBackPressed()
        }

    }


    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}