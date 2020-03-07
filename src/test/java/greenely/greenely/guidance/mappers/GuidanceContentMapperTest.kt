package greenely.greenely.guidance.mappers

import com.nhaarman.mockito_kotlin.*
import greenely.greenely.guidance.models.*
import greenely.greenely.guidance.models.json.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GuidanceContentMapperTest {
    private lateinit var articlesMapper: ArticlesMapper
    private lateinit var offersMapper: OffersMapper
    private lateinit var tipsMapper: TipsMapper
    private lateinit var latestSolarAnalysisMapper: LatestSolarAnalysisMapper
    private lateinit var mapper: GuidanceContentMapper

    private val expectedArticle = Article(
            thumbnailImageUrl = "Some image url",
            thumbnailTitle = "Some title",
            imageUrl = "Some image url",
            title = "Some title",
            text = "Some text",
            link = "Some link",
            label = "Some label",
            id = 0,
            linkText = "Some text"
    )

    private val expectedOffer = Offer(
            thumbnailImageUrl = "Some image url",
            thumbnailTitle = "Some title",
            imageUrl = "Some image url",
            title = "Some title",
            text = "Some text",
            link = "Some link",
            label = "Some label",
            id = 0,
            linkText = "Some text"
    )

    private val expectedTip = Tips(
            thumbnailImageUrl = "Some image url",
            thumbnailTitle = "Some title",
            imageUrl = "Some image url",
            title = "Some title",
            text = "Some text",
            link = "Some link",
            label = "Some label",
            id = 0,
            linkText = "Some text"
    )

    private val expectedLatestSolarAnalysis = LatestSolarAnalysis(
            totalSaving = "12",
            estimatedCostAfterSolarSupport = "12",
            yearlySaving = "12",
            yearlyProduction = "12",
            potentialSaving = "12",
            paybackTimeWithSolarSupport = "12",
            solarPanelLifeSpan = "12",
            createdDate = "2018-01-01",
            _monthData = mutableListOf()
    )

    @Before
    fun setUp() {
        articlesMapper = mock { on { fromArticlesJson(any()) } doReturn expectedArticle }
        offersMapper = mock { on { fromOffersJson(any()) } doReturn expectedOffer }
        tipsMapper = mock { on { fromTipsJson(any()) } doReturn expectedTip }
        latestSolarAnalysisMapper = mock{ on { fromLatestSolarAnalysisJson(any(), any()) } doReturn expectedLatestSolarAnalysis}

        mapper = GuidanceContentMapper(articlesMapper, offersMapper, tipsMapper, latestSolarAnalysisMapper)
    }

    @Test
    fun testFromJson() {
        // Given
        val offerJson = OfferJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )
        val articleJson = ArticleJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )
        val tipJson = TipJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )

        val latestSolarAnalysisJson = LatestSolarAnalysisJson(
                totalSaving = 12,
                estimatedCostAfterSolarSupport = 12,
                yearlySaving = 12,
                yearlyProduction = 12f,
                potentialSaving = 12,
                paybackTimeWithSolarSupport = 12,
                solarPanelLifespan = 12,
                monthData = mutableListOf()
        )
        val responseJson = LatestSolarAnalysisResponseJson(
                id = "12",
                version = 12,
                date = "2018-01-01",
                payload = latestSolarAnalysisJson
        )
        val json = GuidanceContentJson(
                listOf(offerJson),
                listOf(articleJson, articleJson),
                listOf(tipJson),
                responseJson
        )

        // When
        val result = mapper.fromJson(json)

        // Then
        verify(offersMapper, times(1)).fromOffersJson(offerJson)
        verify(articlesMapper, times(2)).fromArticlesJson(articleJson)
        verify(tipsMapper, times(1)).fromTipsJson(tipJson)
        verify(latestSolarAnalysisMapper).fromLatestSolarAnalysisJson(responseJson.payload,responseJson.date)

        assertThat(result).isEqualTo(
                GuidanceContent(
                        listOf(expectedOffer),
                        listOf(expectedTip),
                        listOf(expectedArticle, expectedArticle),
                        expectedLatestSolarAnalysis
                )
        )
    }

    @Test
    fun testFromJsonEmptyLatestAnalysis() {
        // Given
        val offerJson = OfferJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )
        val articleJson = ArticleJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )
        val tipJson = TipJson(
                thumbnailImageUrl = "Some image url",
                thumbnailTitle = "Some title",
                imageUrl = "Some image url",
                title = "Some title",
                text = "Some text",
                link = "Some link",
                label = "Some label",
                id = 0,
                linkText = "Some text"
        )

        val json = GuidanceContentJson(
                listOf(offerJson),
                listOf(articleJson, articleJson),
                listOf(tipJson),
                null
        )

        // When
        val result = mapper.fromJson(json)

        // Then
        verify(offersMapper, times(1)).fromOffersJson(offerJson)
        verify(articlesMapper, times(2)).fromArticlesJson(articleJson)
        verify(tipsMapper, times(1)).fromTipsJson(tipJson)
        verify(latestSolarAnalysisMapper).fromLatestSolarAnalysisJson(null,null)

        assertThat(result).isEqualTo(
                GuidanceContent(
                        listOf(expectedOffer),
                        listOf(expectedTip),
                        listOf(expectedArticle, expectedArticle),
                        null
                )
        )
    }
}