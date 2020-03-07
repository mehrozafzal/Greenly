package greenely.greenely.guidance.models

import android.os.Parcel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely"
)
class ArticleTest {
    @Test
    fun testParcelable() {
        // Given
        val article = Article(
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
        val parcel = Parcel.obtain()

        // When
        article.writeToParcel(parcel, article.describeContents())
        parcel.setDataPosition(0)
        val parsed = Article.CREATOR.createFromParcel(parcel)

        // Then
        assertThat(parsed).isEqualTo(article)
    }
}