package greenely.greenely.signature.ui.events

import android.content.Intent
import greenely.greenely.R
import greenely.greenely.signature.ui.SignatureActivity
import greenely.greenely.tracking.TrackerFactory
import greenely.greenely.utils.pdfview.PdfRendererView

class ReadPoaHandler constructor(
        private val activity: SignatureActivity,
        var next: EventHandler? = null
) : EventHandler {
    override fun handleEvent(event: Event) {
        if (event == Event.ReadPoa) {

            activity.tracker.track(TrackerFactory().trackingEvent(
                    activity.getString(R.string.poa_pdf_preview),
                    activity.getString(R.string.poa_category),"POA Process")
            )

            activity.startActivity(
                    Intent(
                            activity,
                            PdfRendererView::class.java).apply {
                        putExtra(
                                "url",
                                activity.getString(R.string.api_base) + activity.getString(R.string.poa_path)
                        )
                    }
            )
        } else if (event == Event.ReadCombinedPOA) {

            activity.tracker.track(TrackerFactory().trackingEvent(
                    activity.getString(R.string.poa_pdf_preview),
                    activity.getString(R.string.poa_category),"Combined Process")
            )

            activity.startActivity(
                    Intent(
                            activity,
                            PdfRendererView::class.java).apply {
                        putExtra(
                                "url",
                                activity.getString(R.string.api_base) + activity.getString(R.string.combined_poa_path)
                        )
                    }
            )

        } else {
            next?.handleEvent(event)
        }
    }
}