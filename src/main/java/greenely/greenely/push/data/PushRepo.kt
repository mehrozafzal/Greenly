package greenely.greenely.push.data

import android.app.Application
import android.preference.PreferenceManager
import greenely.greenely.api.GreenelyApi
import greenely.greenely.push.data.models.RegisterDeviceRequest
import greenely.greenely.store.UserStore
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        application: Application
) {

    companion object {
        val GCM_TOKEN = "GCM_TOKEN"
        val LAST_NOTIFY_ID_PREF = "LAST_NOTIFY_ID_PREF"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    fun isAuthenticated(): Single<Boolean> {
        return api.checkAuth("JWT ${userStore.token}")
                .map { true }
                .single(false)
    }

    fun setToken(gcmToken: String): Single<Boolean> {
        return api.registerDevice("JWT ${userStore.token}", RegisterDeviceRequest(gcmToken))
                .doOnNext { storeGcmToken(gcmToken) }
                .doOnError { clearToken() }
                .map { true }
                .single(false)
    }

    fun getNextNotificationId(): Int {
        var id = sharedPreferences.getInt(LAST_NOTIFY_ID_PREF, 0) + 1
        if (id == Int.MAX_VALUE) id = 0

        sharedPreferences.edit().putInt(LAST_NOTIFY_ID_PREF, id).apply()

        return id
    }

    private fun clearToken() {
        sharedPreferences.edit().remove(GCM_TOKEN).apply()
    }

    private fun storeGcmToken(gcmToken: String) {
        sharedPreferences.edit().putString(GCM_TOKEN, gcmToken).apply()
    }
}

