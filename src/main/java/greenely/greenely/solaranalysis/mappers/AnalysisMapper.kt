package greenely.greenely.solaranalysis.mappers

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.AnalysisJson
import greenely.greenely.utils.CurrencyFormatUtils
import greenely.greenely.utils.consumptionToString
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@OpenClassOnDebug
class AnalysisMapper @Inject constructor(
        application: Application,
        private val currencyFormatter: CurrencyFormatUtils) {

    private val usageFormat = application.getString(R.string.x_kwh)
    private val yearFormat = application.getString(R.string.x_years)

    fun fromAnalysisJson(json: AnalysisJson, id: String): Analysis {

        return Analysis(
                id = id,
                totalSaving = toCurrencyPresentation(json.totalSaving),
                estimatedCostAfterSolarSupport = toCurrencyPresentation(json.estimatedCostAfterSolarSupport),
                yearlySaving = toCurrencyPresentation(json.yearlySaving),
                yearlyProduction = toUsagePresentation(json.yearlyProduction),
                potentialSaving = toCurrencyPresentation(json.potentialSaving),
                paybackTimeWithSolarSupport = toYearsPresentation(json.paybackTimeWithSolarSupport),
                solarPanelLifeSpan = toYearsPresentation(json.solarPanelLifespan),
                _monthData = json.monthData.toMutableList()
        )
    }

    fun toAnalysisJson(analysis: Analysis): AnalysisJson {
        return AnalysisJson(
                totalSaving = intOfFirstWord(analysis.totalSaving),
                estimatedCostAfterSolarSupport = intOfFirstWord(analysis.estimatedCostAfterSolarSupport),
                yearlySaving = intOfFirstWord(analysis.yearlySaving),
                yearlyProduction = floatOfFirstWord(analysis.yearlyProduction),
                potentialSaving = intOfFirstWord(analysis.potentialSaving),
                paybackTimeWithSolarSupport = intOfFirstWord(analysis.paybackTimeWithSolarSupport),
                solarPanelLifespan = intOfFirstWord(analysis.solarPanelLifeSpan),
                monthData = analysis.monthData
        )
    }

    private fun floatOfFirstWord(value: String): Float = value.split(" ").first().toFloat()

    private fun intOfFirstWord(value: String): Int = value.split(" ").first().toInt()

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

