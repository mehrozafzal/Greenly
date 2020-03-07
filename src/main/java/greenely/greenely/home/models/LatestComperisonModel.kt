package greenely.greenely.home.models

import greenely.greenely.home.data.Comparison

data class LatestComparisonModel(
        val title: String?,
        val comparison: Comparison,
        val maxComparison: Float?
) : ComparisonModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LatestComparisonModel

        if (title != other.title) return false
        if (comparison != other.comparison) return false
        if (maxComparison != other.maxComparison) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + comparison.hashCode()
        result = 31 * result + (maxComparison?.hashCode() ?: 0)
        return result
    }
}
