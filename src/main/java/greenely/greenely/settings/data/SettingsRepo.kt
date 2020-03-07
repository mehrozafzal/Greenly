package greenely.greenely.settings.data

import com.squareup.moshi.Json
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.api.GreenelyApi
import greenely.greenely.store.UserStore
import greenely.greenely.utils.NonNullObservableField
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Information for the settings.
 */
data class SettingsInfo(
        @field:Json(name = "has_poa") val hasPoa: Boolean,
        @field:Json(name = "email") val email: String
)

/**
 * RankResponse wrapper of a [SettingsInfo].
 */
data class SettingsInfoResponse(val account: SettingsInfo)

/**
 * Information about a users household.
 */
data class Household(
        val municipality: String,
        val meter: String,
        @field:Json(name = "facility_area") val facilityArea: String,
        @field:Json(name = "facility_type") val facilityType: String,
        @field:Json(name = "heating_type") val heatingType: String?,
        @field:Json(name = "secondary_heating_type") val secondaryHeatingType: String?,
        @field:Json(name = "tertiary_heating_type") val tertiaryHeatingType: String?,
        @field:Json(name = "quaternary_heating_type") val quaternaryHeatingType: String?,
        @field:Json(name = "electric_car_count") val electricCarCount: String?,
        val occupants: String,
        @field:Json(name = "construction_year") val constructionYear: String?
)

/**
 * Request model for changing a users password.
 */
data class ChangePasswordRequest(
        @field:Json(name = "old_password") val oldPassword: String,
        @field:Json(name = "new_password") val newPassword: String
)

data class NotificationSettingJson(
        @field:Json(name = "is_daily_price_average_push_enabled")
        val isDailyPriceAveragePushEnabled: String,
        @field:Json(name = "is_invoice_alerts_push_enabled")
        val isInvoiceAlertsPushEnabled: String,
        @field:Json(name = "is_monthly_report_email_enabled")
        val isMonthlyReportEmailEnabled: String,
        @field:Json(name = "is_monthly_report_push_enabled")
        val isMonthlyReportPushEnabled: String,
        @field:Json(name = "is_price_alerts_push_enabled")
        val isPriceAlertsPushEnabled: String,
        @field:Json(name = "is_push_enabled")
        val isPushEnabled: String,
        @field:Json(name = "is_weekly_report_push_enabled")
        val isWeeklyReportPushEnabled: String
)


data class NotificationSettingModel(
        private val isDailyPriceAveragePushEnabled: Boolean,
        private val isInvoiceAlertsPushEnabled: Boolean,
        private val isMonthlyReportEmailEnabled: Boolean,
        private val isMonthlyReportPushEnabled: Boolean,
        private val isPriceAlertsPushEnabled: Boolean,
        private val isPushEnabled: Boolean,
        private val isWeeklyReportPushEnabled: Boolean
) {

    val isAllNotificationEnabled = NonNullObservableField(isPushEnabled)
    val isMonthlyPushEnabled = NonNullObservableField(isMonthlyReportPushEnabled)
    val isWeeklyPushEnabled = NonNullObservableField(isWeeklyReportPushEnabled)

    val isDailyPricePushEnabledDisplayValue = NonNullObservableField(false)
    val isInvoicePushEnabledDisplayValue = NonNullObservableField(false)
    val isPricePushEnabledDisplayValue = NonNullObservableField(false)



    fun initRetailDisplayToggleValue(isRetailCustomer: Boolean) {
        if (isRetailCustomer) {
            isDailyPricePushEnabledDisplayValue.set(isDailyPriceAveragePushEnabled)
            isInvoicePushEnabledDisplayValue.set(isInvoiceAlertsPushEnabled)
            isPricePushEnabledDisplayValue.set(isPriceAlertsPushEnabled)

        }
    }

    fun getNotificationSettingRequestObject(isRetailCustomer:Boolean) :NotificationSettingJson =
        if(!isRetailCustomer)
            NotificationSettingJson(isDailyPriceAveragePushEnabled.toString(),
                    isInvoiceAlertsPushEnabled.toString(),
                    isMonthlyReportEmailEnabled.toString(),
                    isMonthlyPushEnabled.get().toString(),
                    isPriceAlertsPushEnabled.toString(),
                    isAllNotificationEnabled.get().toString(),
                    isWeeklyPushEnabled.get().toString())
        else
            NotificationSettingJson(isDailyPricePushEnabledDisplayValue.get().toString(),
                    isInvoicePushEnabledDisplayValue.get().toString(),
                    isMonthlyReportEmailEnabled.toString(),
                    isMonthlyPushEnabled.get().toString(),
                    isPricePushEnabledDisplayValue.get().toString(),
                    isAllNotificationEnabled.get().toString(),
                    isWeeklyPushEnabled.get().toString())




    fun toggleAllSettings(switch: Boolean, isRetailCustomer: Boolean)
    {

        if (!switch) {
            isAllNotificationEnabled.set(false)
            isMonthlyPushEnabled.set(false)
            isWeeklyPushEnabled.set(false)
        }

        if(isRetailCustomer and !switch) {
            isDailyPricePushEnabledDisplayValue.set(false)
            isInvoicePushEnabledDisplayValue.set(false)
            isPricePushEnabledDisplayValue.set(false)

        }

    }


    fun compute(retailSetting :Boolean,isRetailCustomer:Boolean):Boolean
    {
        if(!isRetailCustomer)
            return false
        else return retailSetting
    }


}


/**
 * Implementation of [SettingsRepo] using the api as the model layer.
 */
@OpenClassOnDebug
@Singleton
class SettingsRepo @Inject constructor(
        private val api: GreenelyApi,
        private val userStore: UserStore,
        private val mapper: NotificationMapper
) {

    fun getSettingsInfo(): Observable<SettingsInfo> =
            api.getSettingsInfo("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data.account }

    fun getHousehold(): Observable<Household> =
            api.getHousehold("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { it.data }

    fun changePassword(oldPassword: String, newPassword: String): Observable<Boolean> =
            api.changePassword("JWT ${userStore.token}", ChangePasswordRequest(oldPassword, newPassword))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { true }

    fun getNotificationSettings(): Observable<NotificationSettingModel> =
            api.getNotificationSettings("JWT ${userStore.token}")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { mapper.fromJson(it.data) }

    fun  postNotificationSettings(request:NotificationSettingJson): Observable<NotificationSettingModel> =
            api.patchNotificationSettings("JWT ${userStore.token}",request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { userStore.token = it.jwt }
                    .map { mapper.fromJson(it.data) }
}

