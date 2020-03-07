package greenely.greenely.tracking

import android.app.Application
import javax.inject.Inject

class MainTracker @Inject constructor(
        application: Application
) : Tracker {

    private val tracker = IntercomTracker(SegmentTracker(application))

    override fun trackScreen(screen: String) {
        tracker.trackScreen(screen)
    }

    override fun identify(identifier: Tracker.UserIdentifier) {
        tracker.identify(identifier)
    }

    override fun track(event: Tracker.Event) {
        tracker.track(event)
    }

    override fun reset() {
        tracker.reset()
    }
}

