package greenely.greenely.store

import greenely.greenely.profile.model.Account

/**
 * Interface to a store for user related stuff.
 */
interface UserStore {
    var token: String?
    var userId: Int?
    var firstOpenFlag: Boolean?
    var retailInterestedUsersList: String?
    var deepLinkScreenName: String?
    var appSessionCount: Int
    var isLoggedIn: Boolean?
    var isInvitedUser: Boolean?
    var invitedUserName: String?
    var invitationID: String?
    var isLinkExpired: Boolean?
    var isRegisteredUser: Boolean?
    var isOnboardingShown: Boolean?
    var firstName: String?
    var lastName: String?
    var avatar: String?

    fun incrementAppSessionCount()

    fun isUserHasSelectedInterestedInRetail(): Boolean

    fun clearInvitedUserPreference()

    fun storeAccountsMapToPreference(accountMap: HashMap<Int, Account>)

    fun clearAllTokens()

    fun getAccountsMapFromPreference(): HashMap<Int, Account>

    fun clearAccountsMap()

}

