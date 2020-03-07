package greenely.greenely.tracking

import com.segment.analytics.Properties

class TrackerFactory {

    fun trackingEvent(eventName: String, category: String?, label: String? = null) = object : Tracker.Event {
        override val name: String = eventName
        override val properties: Properties? = Properties()
                .putValue("label", label)
                .putValue("category", category)
    }

}
