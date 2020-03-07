package greenely.greenely.guidance.mappers

import greenely.greenely.guidance.models.Tips
import greenely.greenely.guidance.models.json.TipJson
import org.assertj.core.api.Assertions.*
import org.junit.Test

class TipsMapperTest {
    @Test
    fun testFromTipsJson() {
        //Given
        val mapper = TipsMapper()
        val json = TipJson(
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

        //when
        val result = mapper.fromTipsJson(json)

        //then
        assertThat(result).isEqualTo(
                Tips(
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