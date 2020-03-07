package greenely.greenely.guidance.mappers

import greenely.greenely.guidance.models.Offer
import greenely.greenely.guidance.models.json.OfferJson
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class OffersMapperTest {
    @Test
    fun testFromOfferJson() {
        //Given
        val mapper = OffersMapper()
        val json = OfferJson(
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
        val result = mapper.fromOffersJson(json)

        //then
        assertThat(result).isEqualTo(
                Offer(
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