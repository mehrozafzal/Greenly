package greenely.greenely.guidance.mappers

import greenely.greenely.guidance.models.Article
import greenely.greenely.guidance.models.json.ArticleJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ArticlesMapperTest {
    @Test
    fun testFromArticlesJson() {
        // Given
        val mapper = ArticlesMapper()
        val json = ArticleJson(
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

        // When
        val result = mapper.fromArticlesJson(json)

        // Then
        assertThat(result).isEqualTo(
                Article(
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
        )
    }
}