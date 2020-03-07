package greenely.greenely.tracking

import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.UserAttributes
import io.intercom.android.sdk.identity.Registration

/**
 * Decoration to add intercom tracking.
 */
class IntercomTracker(
        private val tracker: Tracker
) : Tracker by tracker {
    /**
     * Identify the user in intercom.
     */
    override fun identify(identifier: Tracker.UserIdentifier) {
        tracker.identify(identifier)
        if (identifier.userId != null && identifier.email != null && identifier.userHash != null) {
            Intercom.client().apply {
                setUserHash(identifier.userHash)
                registerIdentifiedUser(
                        Registration()
                                .withUserId(identifier.userId.toString())
                                .withUserAttributes(
                                        UserAttributes.Builder()
                                                .withEmail(identifier.email)
                                                .build()
                                )
                )
            }
        }
    }

    override fun track(event: Tracker.Event) {
        tracker.track(event)
    }

    override fun reset() {
        tracker.reset()
        Intercom.client().reset()
    }
}

