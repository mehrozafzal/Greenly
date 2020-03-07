package greenely.greenely.home.models

import android.app.Application
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.home.data.DataState


@OpenClassOnDebug
data class HomeModel(
        val latestComparisonModel: LatestComparisonModel? = null,
        val historicalComparisonModel: HistoricalComparisonModel? = null,
        val extraText: String? = null,
        val stateTitle: String? = null,
        val comparisonInfoText: String? = null,
        val comparisonInfoTitle: String? = null,
        val isButtonVisible: Boolean? = null,
        val resolution: Int? = null,
        val comparisonMaxValue: Float? = null,
        val pointsMaxValue: Float? = null,
        val state: DataState? = null,
        val numFriends: Int? = null,
        val comparisonPercentage: String? = null


) {

    fun isHasDataState(): Boolean = state?.let { it == DataState.HAS_DATA } ?: false
    fun isMissingPOAState(): Boolean = state?.let { it == DataState.NEEDS_POA } ?: false
    fun isWaitingState(): Boolean = state?.let { it == DataState.WAITING } ?: false
    fun isNoFriendsWaitingState(): Boolean = (state == DataState.WAITING && numFriends == 0)

    fun isWaitingForZavanneState(): Boolean = state?.let { it == DataState.WAITING_FOR_ZAVANNE }
            ?: false

    fun isZavanneErrorState(): Boolean = state?.let { it == DataState.ZAVANNE_ERROR } ?: false


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HomeModel

        if (latestComparisonModel != other.latestComparisonModel) return false
        if (historicalComparisonModel != other.historicalComparisonModel) return false
        if (extraText != other.extraText) return false
        if (stateTitle != other.stateTitle) return false
        if (comparisonInfoText != other.comparisonInfoText) return false
        if (comparisonInfoTitle != other.comparisonInfoTitle) return false
        if (isButtonVisible != other.isButtonVisible) return false
        if (resolution != other.resolution) return false
        if (comparisonMaxValue != other.comparisonMaxValue) return false
        if (pointsMaxValue != other.pointsMaxValue) return false
        if (state != other.state) return false
        if (numFriends != other.numFriends) return false
        if (comparisonPercentage != other.comparisonPercentage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latestComparisonModel?.hashCode() ?: 0
        result = 31 * result + (historicalComparisonModel?.hashCode() ?: 0)
        result = 31 * result + (extraText?.hashCode() ?: 0)
        result = 31 * result + (stateTitle?.hashCode() ?: 0)
        result = 31 * result + (comparisonInfoText?.hashCode() ?: 0)
        result = 31 * result + (comparisonInfoTitle?.hashCode() ?: 0)
        result = 31 * result + (isButtonVisible?.hashCode() ?: 0)
        result = 31 * result + (resolution ?: 0)
        result = 31 * result + (comparisonMaxValue?.hashCode() ?: 0)
        result = 31 * result + (pointsMaxValue?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        result = 31 * result + (numFriends?.hashCode() ?: 0)
        result = 31 * result + (comparisonPercentage?.hashCode() ?: 0)
        return result
    }


    @OpenClassOnDebug
    class Builder(
            var context: Application,
            var latestComparisonModel: LatestComparisonModel? = null,
            var historicalComparisonModel: HistoricalComparisonModel? = null,
            var extraText: String? = null,
            var stateTitle: String? = null,
            var isButtonVisible: Boolean? = null,
            var comparisonInfoText: String? = null,
            var comparisonInfoTitle: String? = null,
            var resolution: Int? = null,
            var comparisonMaxValue: Float? = null,
            var pointsMaxValue: Float? = null,
            var state: DataState? = null,
            var numFriends: Int? = null,
            var comparisonPercentage: String? = null

    ) {
        fun build(): HomeModel = HomeModel(
                latestComparisonModel,
                historicalComparisonModel,
                extraText,
                stateTitle,
                comparisonInfoText,
                comparisonInfoTitle,
                isButtonVisible,
                resolution,
                comparisonMaxValue,
                pointsMaxValue,
                state,
                numFriends,
                comparisonPercentage
        )
    }
}