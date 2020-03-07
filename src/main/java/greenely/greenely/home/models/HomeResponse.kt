package greenely.greenely.home.models

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.home.data.Comparison
import greenely.greenely.home.data.DataState

@OpenClassOnDebug
data class HomeResponse(
        val state: DataState,
        val numFriends: Int,
        val points: List<Comparison>,
        val feedback: String?,
        val comparisonInfoTitle: String?,
        val comparisonInfoText: String?,
        val resolution: String?,
        val comparison: Comparison,
        val comparisonMaxValue: Float?,
        val pointsMaxValue: Float?,
        val consumptionPercentage: Int?,
        val consumptionDescription: String?,
        val consumptionDifference: Float?,
        val description: String?

)
{

    val dailyDateRange: String
        get() = (Comparison.dayMonthFormatter.print(points.first().date)+" - "+Comparison.dayMonthFormatter.print(points.last().date)).capitalizeWords()

    val monthlyDateRange: String
        get() = (Comparison.monthFormatter.print(points.first().date)+" - "+Comparison.monthFormatter.print(points.last().date)).capitalizeWords()

    val weeklyDateRange: String
        get() = "Vecka "+Comparison.weekFormatter.print(points.first().date)+" - Vecka "+Comparison.weekFormatter.print(points.last().date)


    val maxRange :Float
      get()= maxOf(comparison.best?:0f,comparison.me?:0f,comparison.others?:0f) * 1.4f
}



fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")



