package greenely.greenely.models

import com.squareup.moshi.Json
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.accountsetup.models.AccountSetupNext
import greenely.greenely.tracking.Tracker

/**
 * Class representing authentication info.
 */
@OpenClassOnDebug
data class AuthenticationInfo(
        @field:Json(name = "user_id") val userId: Int,
        @field:Json(name = "account_setup_next_v3") val accountSetupNext: AccountSetupNext,
        @field:Json(name = "properties") val intercomProperties: Map<String, Any>,
        val intercom: IntercomInfo?
) {
    /**
     * Generate a user identification from the response.
     */
    val userIdentification: Tracker.UserIdentifier
        get() = Tracker.UserIdentifier(userId, intercom?.userHash, intercom?.email, intercomProperties)

    val userIdentificationUpdate: Tracker.UserIdentifier
        get() = Tracker.UserIdentifier(intercom?.userId?.toInt(), intercom?.userHash, intercom?.email, intercomProperties)
}