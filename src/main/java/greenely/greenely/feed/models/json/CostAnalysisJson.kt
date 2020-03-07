package greenely.greenely.feed.models.json

import com.squareup.moshi.Json


data class CostAnalysisJson(
        @field:Json(name = "created")
        val created: Long,
        @field:Json(name = "extra")
        val extra: Extra,
        @field:Json(name = "new_entry")
        val newEntry: Boolean,
        @field:Json(name = "text")
        val text: String,
        @field:Json(name = "title")
        val title: String,
        @field:Json(name = "chart_data")
        val chartData: MutableList<ChartData>


) {
    data class Extra(
            @field:Json(name = "avg_greenely_price")
            val avgGreenelyPrice: Float,
            @field:Json(name = "avg_market_price")
            val avgMarketPrice: Int,
            @field:Json(name = "greenely_cost_in_kr")
            val greenelyCostInKr: Float,
            @field:Json(name = "market_cost_in_kr")
            val marketCostInKr: Float,
            @field:Json(name = "savings_in_kr")
            val savingsInKr: Float,
            @field:Json(name = "subtitle")
            val subtitle: String,
            @field:Json(name = "usage")
            val usage: Float
    )

    data class ChartData(
            @field:Json(name = "greenely_cost_in_kr")
            val greenelyCostInKr: Float,
            @field:Json(name = "greenely_price")
            val greenelyPrice: Float,
            @field:Json(name = "market_cost_in_kr")
            val marketCostInKr: Float,
            @field:Json(name = "market_price")
            val marketPrice: Float,
            @field:Json(name = "timestamp")
            val timestamp: Long,
            @field:Json(name = "usage")
            val usage: Int
    )

}

