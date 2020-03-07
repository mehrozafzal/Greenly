package greenely.greenely.feed.models.feeditems

import greenely.greenely.feed.models.CostAnalysisChartData
import greenely.greenely.feed.models.json.CostAnalysisJson.ChartData
import org.joda.time.DateTime

data class CostAnalysisFeedItem(override val date: DateTime,
                                val title: String,
                                val subtitle: String,
                                val description: String,
                                val marketCost: Float,
                                val savings: Float,
                                val avgMarketPrice : Float,
                                val avgGreenelyPrice : Float,
                                val avgMarketPriceStr:String,
                                val avgGreenelyPriceStr: String,
                                val savingsStr: String,
                                val greenelyCostInKr : Float,
                                val marketCostInKr : Float,
                                val greenelyCostStr:String,
                                val marketCostStr: String,
                                var isNewEntry: Boolean,
                                val chartData: CostAnalysisChartData?):DatedFeedItem {

    override fun read() = this.copy(isNewEntry = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CostAnalysisFeedItem

        if (date != other.date) return false
        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (description != other.description) return false
        if (marketCost != other.marketCost) return false
        if (savings != other.savings) return false
        if (avgMarketPrice != other.avgMarketPrice) return false
        if (avgGreenelyPrice != other.avgGreenelyPrice) return false
        if (avgMarketPriceStr != other.avgMarketPriceStr) return false
        if (avgGreenelyPriceStr != other.avgGreenelyPriceStr) return false
        if (savingsStr != other.savingsStr) return false
        if (greenelyCostInKr != other.greenelyCostInKr) return false
        if (marketCostInKr != other.marketCostInKr) return false
        if (greenelyCostStr != other.greenelyCostStr) return false
        if (marketCostStr != other.marketCostStr) return false
        if (isNewEntry != other.isNewEntry) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + marketCost.hashCode()
        result = 31 * result + savings.hashCode()
        result = 31 * result + avgMarketPrice.hashCode()
        result = 31 * result + avgGreenelyPrice.hashCode()
        result = 31 * result + avgMarketPriceStr.hashCode()
        result = 31 * result + avgGreenelyPriceStr.hashCode()
        result = 31 * result + savingsStr.hashCode()
        result = 31 * result + greenelyCostInKr.hashCode()
        result = 31 * result + marketCostInKr.hashCode()
        result = 31 * result + greenelyCostStr.hashCode()
        result = 31 * result + marketCostStr.hashCode()
        result = 31 * result + isNewEntry.hashCode()
        result = 31 * result + (chartData?.hashCode() ?: 0)
        return result
    }


}