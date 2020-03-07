package greenely.greenely.feed.models

import com.squareup.moshi.Json
import greenely.greenely.feed.models.json.*

data class FeedResponse(
        @field:Json(name = "feed_messages") val messages: List<MessageJson>,
        @field:Json(name = "day_high_lows") val dayMinMax: List<MinMaxJson>,
        @field:Json(name = "month_availables") val monthlyReports: List<MonthlyReportJson>,
        @field:Json(name = "feed_tips") val tips: List<TipJson>,
        @field:Json(name = "feed_news") val news: List<NewsJson>,
        @field:Json(name = "week_comparisons") val weeklyReports: List<WeeklyReportJson>,
        @field:Json(name = "cost_analysis") val costAnalysis: List<CostAnalysisJson>,
        @field:Json(name = "predicted_invoices") val predictedInvoices: List<PredictedInvoiceJson>,
        @field:Json(name = "predicted_yearly_savings") val predictedYearlySaving: List<PredictedYearlySavingsJson>


) {
    /**
     * Convert a feedItems response to a list of feedItems items.
     */
    fun toList() = listOf(messages, dayMinMax, monthlyReports, tips, news, weeklyReports).flatten()
}
