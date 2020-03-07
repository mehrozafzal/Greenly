package greenely.greenely.tracking

import com.segment.analytics.Properties
import greenely.greenely.OpenClassOnDebug

interface Tracker {
    fun trackScreen(screen: String)
    fun identify(identifier: UserIdentifier)
    fun track(event: Event)
    fun reset()

    @OpenClassOnDebug
    data class UserIdentifier(
            val userId: Int? = null,
            val userHash: String? = null,
            val email: String? = null,
            val properties: Map<String, Any>
    )

    interface Event {
        /**
         * The name of the event.
         */
        val name: String

        /**
         * The properties of the event.
         */
        val properties: Properties?
    }
}

