package greenely.greenely.settings.faq.mappers

import greenely.greenely.settings.faq.ui.models.FaqItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class FaqItemMapperTest {
    private lateinit var mapper: FaqItemMapper

    @Before
    fun setUp() {
        mapper = FaqItemMapper()
    }

    @Test
    fun testFromJson() {
        // Given
        val json = listOf(listOf("Question", "Answer"))

        // When
        val result = mapper.fromJson(json)

        // Then
        assertThat(result).isEqualTo(listOf(FaqItem("Question", "Answer")))
    }

    @Test
    fun testFromJsonIncomplete() {
        // Given
        val json = listOf(listOf("Question", "Answer"), listOf("Question"))

        // When
        val result = mapper.fromJson(json)

        // Then
        assertThat(result).isEqualTo(listOf(FaqItem("Question", "Answer")))
    }
}