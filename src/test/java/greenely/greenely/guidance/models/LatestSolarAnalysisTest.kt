package greenely.greenely.guidance.models

import android.os.Parcel
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        sdk = [21],
        packageName = "greenely.greenely"
)
class LatestSolarAnalysisTest {
    @Test
    fun testParcelable() {
        // Given
        val latestAnalysis = LatestSolarAnalysis(
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
        val parcel = Parcel.obtain()

        // When
        latestAnalysis.writeToParcel(parcel, latestAnalysis.describeContents())
        parcel.setDataPosition(0)
        val parsed = LatestSolarAnalysis.CREATOR.createFromParcel(parcel)

        // Then
        Assertions.assertThat(parsed).isEqualTo(latestAnalysis)
    }
}
