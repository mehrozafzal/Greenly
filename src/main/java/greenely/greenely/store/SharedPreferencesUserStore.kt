package greenely.greenely.store

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import greenely.greenely.profile.model.Account
import org.json.JSONObject
import javax.inject.Inject

class SharedPreferencesUserStore @Inject constructor(application: Application) : UserStore {


    companion object {
        val USER_ID_KEY = "USER_ID_KEY"
        val TOKEN_KEY = "TOKEN_KEY"
        val FIRST_OPEN_FLAG = "FIRST_OPEN_FLAG"
        val RETAIL_INTERESTED_USERS_LIST = "RETAIL_INTERESTED_USERS_LIST"
        val DEEP_LINK_SCREEN_NAME = "DEEP_LINK_SCREEN_NAME"
        val APP_SESSION_COUNT = "APP_SESSION_COUNT"
        val IS_LOGGED_IN = "IS_LOGGED_IN"
        val IS_INVITED_USER = "IS_INVITED_USER"
        val INVITED_USER_NAME = "INVITED_USER_NAME"
        val INVITATION_ID = "INVITATION_ID"
        val INVITATION_LINK_EXPIRED = "INVITATION_LINK_EXPIRED"
        val IS_REGISTERED_USER = "IS_REGISTERED_USER"
        val IS_ONBOARDING_SHOWN = "IS_ONBOARDING_SHOWN"
        val USER_FIRST_NAME = "USER_FIRST_NAME"
        val USER_LAST_NAME = "USER_LAST_NAME"
        val USER_AVATAR = "USER_AVATAR"
        val ACCOUNTS_MAP = "ACCOUNTS_MAP"

    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    override var token: String?
        get() = preferences.getString(TOKEN_KEY, null)
        set(value) {
            if (value != null) {
                preferences.edit().putString(TOKEN_KEY, value).apply()
            } else {
                preferences.edit().remove(TOKEN_KEY).apply()
            }
        }

    override var isInvitedUser: Boolean?
        get() = preferences.getBoolean(IS_INVITED_USER, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(IS_INVITED_USER, value).apply()
            } else {
                preferences.edit().remove(IS_INVITED_USER).apply()
            }
        }

    override var invitedUserName: String?
        get() = preferences.getString(INVITED_USER_NAME, null)
        set(value) {
            if (value != null) {
                preferences.edit().putString(INVITED_USER_NAME, value).apply()
            } else preferences.edit().remove(INVITED_USER_NAME).apply()
        }
    override var invitationID: String?
        get() = preferences.getString(INVITATION_ID, null)
        set(value) {
            if (value != null) {
                preferences.edit().putString(INVITATION_ID, value).apply()
            } else preferences.edit().remove(INVITATION_ID).apply()
        }
    override var isLinkExpired: Boolean?
        get() = preferences.getBoolean(INVITATION_LINK_EXPIRED, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(INVITATION_LINK_EXPIRED, value).apply()
            } else preferences.edit().remove(INVITATION_LINK_EXPIRED).apply()
        }
    override var isRegisteredUser: Boolean?
        get() = preferences.getBoolean(IS_REGISTERED_USER, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(IS_REGISTERED_USER, value).apply()
            } else preferences.edit().remove(IS_REGISTERED_USER).apply()
        }
    override var isOnboardingShown: Boolean?
        get() = preferences.getBoolean(IS_ONBOARDING_SHOWN, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(IS_ONBOARDING_SHOWN, value).apply()
            } else preferences.edit().remove(IS_ONBOARDING_SHOWN).apply()
        }
    override var firstName: String?
        get() = preferences.getString(USER_FIRST_NAME, "")
        set(value) {
            if (value != null) {
                preferences.edit().putString(USER_FIRST_NAME, value).apply()
            } else {
                preferences.edit().remove(USER_FIRST_NAME).apply()
            }
        }
    override var lastName: String?
        get() = preferences.getString(USER_LAST_NAME, "")
        set(value) {
            if (value != null) {
                preferences.edit().putString(USER_LAST_NAME, value).apply()
            } else {
                preferences.edit().remove(USER_LAST_NAME).apply()
            }
        }
    override var avatar: String?
        get() = preferences.getString(USER_AVATAR, "")
        set(value) {
            if (value != null) {
                preferences.edit().putString(USER_AVATAR, value).apply()
            } else {
                preferences.edit().remove(USER_AVATAR).apply()
            }
        }

    override var appSessionCount: Int
        get() = preferences.getInt(APP_SESSION_COUNT, 3) //To change initializer of created properties use File | Settings | File Templates.
        set(value) {
            preferences.edit().putInt(APP_SESSION_COUNT, value).apply()
        }

    override var userId: Int?
        get() = preferences.getInt(USER_ID_KEY, -1).let { if (it == -1) null else it }
        set(value) {
            if (value != null) {
                preferences.edit().putInt(USER_ID_KEY, value).apply()
            } else {
                preferences.edit().remove(USER_ID_KEY).apply()
            }
        }

    override var isLoggedIn: Boolean?
        get() = preferences.getBoolean(IS_LOGGED_IN, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(IS_LOGGED_IN, value).apply()
            } else preferences.edit().putBoolean(IS_LOGGED_IN, false).apply()
        }

    override var firstOpenFlag: Boolean?
        get() = preferences.getBoolean(FIRST_OPEN_FLAG, false)
        set(value) {
            if (value != null) {
                preferences.edit().putBoolean(FIRST_OPEN_FLAG, value).apply()
            } else preferences.edit().putBoolean(FIRST_OPEN_FLAG, false).apply()
        }

    override var retailInterestedUsersList: String?
        get() = preferences.getString(RETAIL_INTERESTED_USERS_LIST, arrayListOf<Int>().toString())
        set(value) {
            if (value != null) {
                preferences.edit().putString(RETAIL_INTERESTED_USERS_LIST, value).apply()
            } else preferences.edit().remove(RETAIL_INTERESTED_USERS_LIST).apply()
        }

    override var deepLinkScreenName: String?
        get() = preferences.getString(DEEP_LINK_SCREEN_NAME, null)
        set(value) {
            if (value != null) {
                preferences.edit().putString(DEEP_LINK_SCREEN_NAME, value).apply()
            } else {
                preferences.edit().remove(DEEP_LINK_SCREEN_NAME).apply()
            }
        }

    override fun incrementAppSessionCount() {
        appSessionCount++
    }


    private fun getStoredInterestedUsersList(): ArrayList<Int> {
        val gson = Gson()
        val json = retailInterestedUsersList
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(json, type)
    }

    override fun isUserHasSelectedInterestedInRetail() = getStoredInterestedUsersList().contains(this.userId)

    override fun clearInvitedUserPreference() {
        preferences.edit().putString(IS_LOGGED_IN, "").apply()
        preferences.edit().putBoolean(IS_INVITED_USER, false).apply()
        preferences.edit().putBoolean(IS_REGISTERED_USER, false).apply()
        preferences.edit().putBoolean(INVITATION_LINK_EXPIRED, false).apply()
        preferences.edit().putString(INVITED_USER_NAME, "").apply()
        preferences.edit().putString(INVITATION_ID, "").apply()

    }

    override fun storeAccountsMapToPreference(accountMap: HashMap<Int, Account>) {
        val jsonString: String = Gson().toJson(accountMap)
        preferences.edit().putString(ACCOUNTS_MAP, jsonString).apply()
    }

    override fun getAccountsMapFromPreference(): HashMap<Int, Account> {
        val jsonString: String? = preferences.getString(ACCOUNTS_MAP, JSONObject().toString())
        return if (!jsonString.equals("{}")) {
            val listType = object : TypeToken<Map<Int, Account>>() {}.type
            Gson().fromJson(jsonString, listType)
        } else
            HashMap()
    }

    override fun clearAllTokens() {
        val accountMap = getAccountsMapFromPreference()
        for ((key, value) in accountMap) {
            val account: Account? = value
            account?.token = null
            account?.let { it1 -> accountMap.put(key, it1) }
        }
        storeAccountsMapToPreference(accountMap)
    }

    override fun clearAccountsMap() {
        val accountMap = getAccountsMapFromPreference()
        accountMap.clear()
        storeAccountsMapToPreference(accountMap)
    }


}