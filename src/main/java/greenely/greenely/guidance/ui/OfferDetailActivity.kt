package greenely.greenely.guidance.ui

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import dagger.android.AndroidInjection
import greenely.greenely.R
import greenely.greenely.databinding.GuidanceOfferDetailBinding
import greenely.greenely.guidance.models.Offer
import greenely.greenely.tracking.Tracker
import javax.inject.Inject

class OfferDetailActivity : AppCompatActivity() {

    lateinit var binding: GuidanceOfferDetailBinding

    @Inject
    lateinit var tracker: Tracker

    private lateinit var itemTrackName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.guidance_offer_detail)

        bindOffer()

        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }

        binding.detailText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindOffer() {
        val offer = intent.extras.getParcelable<Offer>("offer")
        binding.offer = offer
        itemTrackName = offer.thumbnailTitle + " " + offer.id
    }

    override fun onResume() {
        super.onResume()
        tracker.trackScreen("GU Offers " + itemTrackName)
    }
}