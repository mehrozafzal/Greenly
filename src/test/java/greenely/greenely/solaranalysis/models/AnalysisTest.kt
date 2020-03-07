package greenely.greenely.solaranalysis.models

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
class AnalysisTest {
    @Test
    fun testParcel() {
        // Given
        val analysis = Analysis(
                "1234",
                "1234",
                "1234",
                "1234",
                "1234",
                "1234",
                "1234",
                "1234",
                mutableListOf(1.0f))
        val parcel = Parcel.obtain()

        // When
        analysis.writeToParcel(parcel, analysis.describeContents())
        parcel.setDataPosition(0)
        val parsed = Analysis.CREATOR.createFromParcel(parcel)

        // Then
        assertThat(parsed).isEqualTo(analysis)
    }
}