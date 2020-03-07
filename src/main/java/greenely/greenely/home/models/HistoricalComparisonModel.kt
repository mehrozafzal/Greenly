package greenely.greenely.home.models

import greenely.greenely.home.data.Comparison

data class HistoricalComparisonModel(
        val points: List<Comparison>,
        val title: String?,
        val pointsMaxValue:Float?,
        val consumptionDifference:String?,
        val consumptionDescription:String?

        ) :ComparisonModel

{
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as HistoricalComparisonModel

                if (points != other.points) return false
                if (title != other.title) return false
                if (pointsMaxValue != other.pointsMaxValue) return false
                if (consumptionDifference != other.consumptionDifference) return false
                if (consumptionDescription != other.consumptionDescription) return false

                return true
        }

        override fun hashCode(): Int {
                var result = points.hashCode()
                result = 31 * result + (title?.hashCode() ?: 0)
                result = 31 * result + (pointsMaxValue?.hashCode() ?: 0)
                result = 31 * result + (consumptionDifference?.hashCode() ?: 0)
                result = 31 * result + (consumptionDescription?.hashCode() ?: 0)
                return result
        }
}
