package greenely.greenely.home.data

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.R
import greenely.greenely.home.models.*
import greenely.greenely.utils.toCurrenyWithDecimalCheck
import javax.inject.Inject

/**
 * Chain that processes a home response.
 */
abstract class HomeResponseProcessor {

    /**
     * Next processor in the chain.
     */
    open var next: HomeResponseProcessor? = null

    /**
     * Process the response.
     */
    abstract fun processResponse(response: HomeResponse): HomeResponse
}

class ProcessHasData(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        if (response.state == DataState.HAS_DATA) {
            homeModelBuilder.extraText = response.description
            homeModelBuilder.stateTitle = getTitle(response)
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.isButtonVisible = false
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends
            homeModelBuilder.comparisonPercentage = response.consumptionPercentage?.let { it.toString() }
                    ?: "--"
        }

        return response.let { next?.processResponse(it) ?: it }
    }

}

class ProcessMissingPoa(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        if (response.state == DataState.NEEDS_POA) {
            homeModelBuilder.extraText = homeModelBuilder.context.getString(R.string.missing_poa_text)
            homeModelBuilder.stateTitle = homeModelBuilder.context.getString(R.string.missing_poa_state_title)
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.isButtonVisible = true
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends

        }

        return response.let { next?.processResponse(it) ?: it }
    }
}

class ProcessWaiting(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        if (response.state == DataState.WAITING && response.numFriends != 0) {
            homeModelBuilder.extraText = homeModelBuilder.context.getString(R.string.waiting_for_data_text)
            homeModelBuilder.stateTitle = getTitle(response)
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.isButtonVisible = false
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends

        } else if (response.state == DataState.WAITING && response.numFriends == 0) {
            homeModelBuilder.extraText = homeModelBuilder.context.getString(R.string.waiting_for_data_text_no_friends)
            homeModelBuilder.stateTitle = homeModelBuilder.context.getString(R.string.home_state_title)
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.isButtonVisible = false
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends

        }
        return response.let { next?.processResponse(it) ?: it }
    }
}

class ProcessWaitingForZavanne(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        if (response.state == DataState.WAITING_FOR_ZAVANNE) {
            homeModelBuilder.extraText = homeModelBuilder.context.getString(R.string.waiting_for_zavanne_text)
            homeModelBuilder.stateTitle = getTitle(response)
            homeModelBuilder.isButtonVisible = false
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends


        }
        return response.let { next?.processResponse(response) ?: it }
    }
}

class ProcessZavanneError(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        if (response.state == DataState.ZAVANNE_ERROR) {
            homeModelBuilder.extraText = homeModelBuilder.context.getString(R.string.zavanne_error_text)
            homeModelBuilder.stateTitle = homeModelBuilder.context.getString(R.string.zavanne_error_state_title)
            homeModelBuilder.comparisonInfoText = response.comparisonInfoText
            homeModelBuilder.comparisonInfoTitle = response.comparisonInfoTitle
            homeModelBuilder.isButtonVisible = true
            homeModelBuilder.state = response.state
            homeModelBuilder.numFriends = response.numFriends


        }

        return response.let { next?.processResponse(it) ?: it }
    }
}

class ProcessComparisonData(
        private val homeModelBuilder: HomeModel.Builder
) : HomeResponseProcessor() {
    override fun processResponse(response: HomeResponse): HomeResponse {
        homeModelBuilder.latestComparisonModel = LatestComparisonModel(
                title = getTitle(response),
                comparison = response.comparison,
                maxComparison = response.maxRange
        )
        homeModelBuilder.historicalComparisonModel = HistoricalComparisonModel(
                points = response.points,
                title = getHistoricalChartTitle(response),
                pointsMaxValue = response.pointsMaxValue,
                consumptionDifference = response.consumptionDifference?.let { (it / 1000).toCurrenyWithDecimalCheck() }
                        ?: "--",
                consumptionDescription = response.consumptionDescription
        )
        homeModelBuilder.resolution = getResolution(response)
        homeModelBuilder.comparisonMaxValue = response.comparisonMaxValue // todo this may not required after version 6.2.0
        homeModelBuilder.pointsMaxValue = response.pointsMaxValue // todo this may not required after version 6.2.0
        homeModelBuilder.numFriends = response.numFriends

        return response.let { next?.processResponse(it) ?: it }
    }


    private fun getHistoricalChartTitle(response: HomeResponse): String = when (response.resolution) {
        "D" -> response.dailyDateRange
        "W" -> response.weeklyDateRange
        "M" -> response.monthlyDateRange
        else -> ""
    }


    private fun getResolution(response: HomeResponse): Int = when (response.resolution) {
        "D" -> R.id.days
        "W" -> R.id.weeks
        "M" -> R.id.months
        else -> throw IllegalArgumentException()
    }
}

/**
 * Factory for creating a [HomeResponseProcessor].
 */
@OpenClassOnDebug
class HomeModelFactory @Inject constructor(val context: Application) {

    /**
     * Create a [HomeModel].
     */
    fun createHomeModel(
            homeResponse: HomeResponse
    ): HomeModel {
        val builder = HomeModel.Builder(context)

        val processHasData = ProcessHasData(builder)
        val processMissingPoa = ProcessMissingPoa(builder)
        val processWaiting = ProcessWaiting(builder)
        val processZavanneError = ProcessZavanneError(builder)
        val processWaitingForZavanne = ProcessWaitingForZavanne(builder)
        val processComparisonData = ProcessComparisonData(builder)

        processHasData.next = processMissingPoa
        processMissingPoa.next = processWaiting
        processWaiting.next = processZavanneError
        processZavanneError.next = processWaitingForZavanne
        processWaitingForZavanne.next = processComparisonData

        processHasData.processResponse(homeResponse)

        return builder.build()
    }
}

fun getTitle(response: HomeResponse): String = when (response.resolution) {
    "D" -> response.comparison.day?.capitalizeWords() ?: ""
    "W" -> "Vecka " + response.comparison.weekNumber
    "M" -> response.comparison.monthName?.capitalize() ?: ""
    else -> ""
}

