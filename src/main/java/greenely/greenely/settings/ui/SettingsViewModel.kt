package greenely.greenely.settings.ui

import android.app.Activity
import androidx.lifecycle.*
import android.content.Intent
import android.widget.CompoundButton
import greenely.greenely.main.ui.MainActivity
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.settings.data.Household
import greenely.greenely.settings.data.NotificationSettingModel
import greenely.greenely.settings.data.SettingsInfo
import greenely.greenely.settings.data.SettingsRepo
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


/**
 * Events that might occur in settings
 */
enum class Event {
    /**
     * When the password is changed.
     */
    PASSWORD_CHANGED
}

/**
 * Events to be sent to the Ui.
 */
sealed class UiEvent {
    /**
     * When the password has been changed.
     */
    object PasswordChanged : UiEvent()

    /**
     * When an error should be shown.
     *
     * @property error The error the occured.
     */
    data class ShowError(val error: Throwable) : UiEvent()
}


/**
 * View model for settings
 */
class SettingsViewModel @Inject constructor(
        val repo: SettingsRepo,
        val retailStateHandler: RetailStateHandler
) : ViewModel() {
    private val loadingInfo = MutableLiveData<Boolean>()
    private val loadingHouseHold = MutableLiveData<Boolean>()
    private val loadingChangePassword = MutableLiveData<Boolean>()
    private val loadingNotificationSettings = MutableLiveData<Boolean>()

    private var settingsInfo = MutableLiveData<SettingsInfo>()
     var household = MutableLiveData<Household>()
     var notificationSettings = MutableLiveData<NotificationSettingModel>()

    var onAllNotificationToggleClickListener: CompoundButton.OnCheckedChangeListener? = null

    var onRetailSettingsToggleClickListener: CompoundButton.OnCheckedChangeListener? = null

    var commonToggleClickListener: CompoundButton.OnCheckedChangeListener? = null





    private val events = SingleLiveEvent<UiEvent>()


    /**
     * Get the settings
     */
    fun getSettings(): LiveData<SettingsInfo> {
        if (settingsInfo.value == null) fetchSettingsInfo()
        return settingsInfo
    }

    /**
     * Get a stream of events.
     */
    fun getEvents(): LiveData<UiEvent> = events

    /**
     * Get the household.
     */
    fun getHousehold(): LiveData<Household> {
//        if (household.value == null) fetchHousehold()
        fetchHousehold()
        return household
    }

    fun getNotificationSettings():LiveData<NotificationSettingModel>{
         resetNotificationSettings()
         fetchNotificationSettings()
        return notificationSettings
    }

    fun resetNotificationSettings() {
        notificationSettings.value=null
    }

    /**
     * If teh settings are loading.
     */
    fun isLoadingSettingsInfo(): LiveData<Boolean> = loadingInfo

    /**
     * If the household is loading.
     */
    fun isLoadingHousehold(): LiveData<Boolean> = loadingHouseHold

    /**
     * If change password is loading.
     */
    fun isLoadingChangePassword(): LiveData<Boolean> = loadingChangePassword

    /**
     * Change the password.
     *
     * @param oldPassword The old password.
     * @param newPassword The new password
     */
    fun changePassword(oldPassword: String, newPassword: String) {
        repo.changePassword(oldPassword, newPassword)
                .doOnSubscribe { loadingChangePassword.value = true }
                .doOnTerminate { loadingChangePassword.value = false }
                .doOnDispose { loadingChangePassword.value = false }
                .subscribeBy(
                        onNext = {
                            events.value = UiEvent.PasswordChanged
                        },
                        onError = {
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    fun fetchRetailState()= retailStateHandler.refreshRetailState()


    /**
     * Called when getting activity results.
     */
    fun onActivityResult(requestCode: Int, responseCode: Int, @Suppress("UNUSED_PARAMETER") intent: Intent?) {
        if (requestCode == MainActivity.SIGN_POA_REQUEST && responseCode == Activity.RESULT_OK) {
            fetchHousehold()
            fetchSettingsInfo()
        }
    }

    private fun fetchHousehold() {
        repo.getHousehold()
                .doOnSubscribe { loadingHouseHold.value = true }
                .doOnTerminate { loadingHouseHold.value = false }
                .doOnDispose { loadingHouseHold.value = false }
                .subscribeBy(
                        onNext = {
                            household.value = it
                        },
                        onError = {
                            household.value = null
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchSettingsInfo() {
        repo.getSettingsInfo()
                .doOnSubscribe { loadingInfo.value = true }
                .doOnDispose { loadingInfo.value = false }
                .doOnTerminate { loadingInfo.value = false }
                .subscribeBy(
                        onNext = {
                            settingsInfo.value = it
                        },
                        onError = {
                            settingsInfo.value = null
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }

    private fun fetchNotificationSettings() {
        repo.getNotificationSettings()
                .doOnSubscribe { loadingNotificationSettings.value = true }
                .doOnDispose { loadingNotificationSettings.value = false }
                .doOnTerminate { loadingNotificationSettings.value = false }
                .subscribeBy(
                        onNext = {
                            notificationSettings.value = it
                        },
                        onError = {
                            notificationSettings.value = null
                            events.value = UiEvent.ShowError(it)
                        }
                )
    }


     fun onAllNotificationToggleClick(switch:Boolean) {

         retailStateHandler.isRetailCustomer.get().let {
             notificationSettings.value?.let {
                 it.toggleAllSettings(switch,retailStateHandler.isRetailCustomer.get())
             }
         }
    }

    fun isRetailCustomer() =retailStateHandler.isRetailCustomer.get()



    fun postNotificationSetting(){
        notificationSettings.value?.let {
            repo.postNotificationSettings(it.getNotificationSettingRequestObject(retailStateHandler.isRetailCustomer.get()))
                    .subscribeBy(
                            onNext = {
                            },
                            onError = {

                            }
                    )
        }
    }







}

