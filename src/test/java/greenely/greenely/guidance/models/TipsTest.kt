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
class TipsTest {
    @Test
    fun testParcelable() {
        //Given
        val tip = Tips(
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
        tip.writeToParcel(parcel, tip.describeContents())
        parcel.setDataPosition(0)
        val parsed = Tips.CREATOR.createFromParcel(parcel)

        // Then
        assertThat(parsed).isEqualTo(tip)

    }
}