package greenely.greenely.settings.data

import javax.inject.Inject

class NotificationMapper @Inject constructor() {

    fun fromJson(response: NotificationSettingJson) =

      NotificationSettingModel(
              isDailyPriceAveragePushEnabled = response.isDailyPriceAveragePushEnabled.toBoolean(),
              isInvoiceAlertsPushEnabled = response.isInvoiceAlertsPushEnabled.toBoolean(),
              isMonthlyReportEmailEnabled=response.isMonthlyReportEmailEnabled.toBoolean(),
              isMonthlyReportPushEnabled=response.isMonthlyReportPushEnabled.toBoolean(),
              isPriceAlertsPushEnabled=response.isPriceAlertsPushEnabled.toBoolean(),
              isPushEnabled=response.isPushEnabled.toBoolean(),
              isWeeklyReportPushEnabled=response.isWeeklyReportPushEnabled.toBoolean()
      )

}

