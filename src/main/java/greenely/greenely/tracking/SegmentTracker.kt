@file:Suppress("KDocMissingDocumentation")

package greenely.greenely.tracking

import android.content.Context
import com.segment.analytics.Analytics
import com.segment.analytics.Traits

class SegmentTracker(
        private val context: Context
) : Tracker {

    override fun trackScreen(screen: String) {
        Analytics.with(context).apply {
            analyticsContext.putValue("ip", "0.0.0.0")
            screen(screen)
        }
    }

    override fun identify(identifier: Tracker.UserIdentifier) {
        identifier.userId?.let {
            Analytics.with(context).apply {
                analyticsContext.putValue("ip", "0.0.0.0")
                identify(it.toString(), userTraits(identifier), null)
            }
        }
    }

    override fun track(event: Tracker.Event) {
        Analytics.with(context).apply {
            analyticsContext.putValue("ip", "0.0.0.0")
            track(event.name, event.properties)
        }
    }

    override fun reset() {
        Analytics.with(context).apply {
            analyticsContext.putValue("ip", "0.0.0.0")
            reset()
        }

    }

    private fun userTraits(identifier: Tracker.UserIdentifier): Traits {
        val traits = Traits()
        identifier.properties.forEach {
            traits.putValue(it.key, it.value)
        }
        return traits
    }
}

