package greenely.greenely.guidance.models.json

import com.squareup.moshi.Json

data class GuidanceContentJson(
        @field:Json(name = "is_solar_analysis_enabled")
        val isSolarAnalysisEnabled: Boolean,
        @field:Json(name = "offers")
        val offers: List<OfferJson>,
        @field:Json(name = "articles_guides")
        val articles: List<ArticleJson>,
        @field:Json(name = "tips")
        val tips: List<TipJson>,
        @field:Json(name = "latest_solar_analysis")
        val latestSolarAnalysisResponse: LatestSolarAnalysisResponseJson?
)