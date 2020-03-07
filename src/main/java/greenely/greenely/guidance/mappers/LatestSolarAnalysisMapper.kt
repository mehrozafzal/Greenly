package greenely.greenely.guidance.mappers

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.guidance.models.LatestSolarAnalysis
import greenely.greenely.guidance.models.json.LatestSolarAnalysisJson
import greenely.greenely.utils.CurrencyFormatUtils
import greenely.greenely.utils.consumptionToString
import javax.inject.Inject

@OpenClassOnDebug
class LatestSolarAnalysisMapper @Inject constructor(
        application: Application,
        private val currencyFormatter: CurrencyFormatUtils) {

    private val usageFormat = application.getString(R.string.x_kwh)
    private val yearFormat = application.getString(R.string.x_years)

    fun fromLatestSolarAnalysisJson(json: LatestSolarAnalysisJson?, date: String?): LatestSolarAnalysis? {
        return if (json != null && date != null) {
            LatestSolarAnalysis(
                    totalSaving = toCurrencyPresentation(json.totalSaving),
                    estimatedCostAfterSolarSupport = toCurrencyPresentation(json.estimatedCostAfterSolarSupport),
                    yearlySaving = toCurrencyPresentation(json.yearlySaving),
                    yearlyProduction = toUsagePresentation(json.yearlyProduction),
                    potentialSaving = toCurrencyPresentation(json.potentialSaving),
                    paybackTimeWithSolarSupport = toYearsPresentation(json.paybackTimeWithSolarSupport),
                    solarPanelLifeSpan = toYearsPresentation(json.solarPanelLifespan),
                    createdDate = date,
                    _monthData = json.monthData.toMutableList()
            )
        } else return null
    }

    private fun toCurrencyPresentation(value: Int): String {
        return currencyFormatter.currencyFormatter(value)
    }

    private fun toUsagePresentation(value: Float): String {
        return usageFormat.format(value.consumptionToString())
    }

    private fun toYearsPresentation(value: Int): String {
        return yearFormat.format(value.toString())
    }

}
