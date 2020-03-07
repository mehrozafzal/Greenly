package greenely.greenely.guidance.ui

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceTipDetailBinding
import greenely.greenely.guidance.models.Tips
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class TipsDetailActivity : AppCompatActivity() {

    lateinit var binding: GuidanceTipDetailBinding

    @Inject
    lateinit var tracker: Tracker

    private lateinit var itemTrackName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.guidance_tip_detail)

        bindTip()

        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }

        binding.detailText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindTip() {
        val tip = intent.extras.getParcelable<Tips>("tip")
        binding.tip = tip
        itemTrackName = tip.thumbnailTitle + " " + tip.id
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("GU Tips " + itemTrackName)
    }
}
