package greenely.greenely.guidance.mappers

import greenely.greenely.guidance.models.GuidanceContent
import greenely.greenely.guidance.models.json.GuidanceContentJson
import javax.inject.Inject

class GuidanceContentMapper @Inject constructor(
        private val articlesMapper: ArticlesMapper,
        private val offersMapper: OffersMapper,
        private val tipsMapper: TipsMapper,
        private val latestSolarAnalysisMapper: LatestSolarAnalysisMapper
) {

    fun fromJson(json: GuidanceContentJson): GuidanceContent {
        return GuidanceContent(
                isSolarAnalysisEnabled = json.isSolarAnalysisEnabled,
                offers = json.offers.map {
                    offersMapper.fromOffersJson(it)
                },
                tips = json.tips.map { tipsMapper.fromTipsJson(it) },
                articles = json.articles.map { articlesMapper.fromArticlesJson(it) },
                latestSolarAnalysis = latestSolarAnalysisMapper.fromLatestSolarAnalysisJson(
                        json.latestSolarAnalysisResponse?.payload, json.latestSolarAnalysisResponse?.date)
        )
    }
}
