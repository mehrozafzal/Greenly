package greenely.greenely.guidance.models

data class GuidanceContent(
        val isSolarAnalysisEnabled: Boolean,
        val offers: List<Offer>,
        val tips: List<Tips>,
        val articles: List<Article>,
        val latestSolarAnalysis: LatestSolarAnalysis?
)
