package greenely.greenely.solaranalysis.models

import androidx.databinding.ObservableInt
import android.os.Parcel
import greenely.greenely.utils.NonNullObservableField
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        packageName = "greenely.greenely",
        sdk = intArrayOf(21)
)
class HouseholdInfoTest {
    @Test
    fun testParcelableMemento() {
        // Given
        val inputData = HouseholdInfo(
                NonNullObservableField("Address"),
                NonNullObservableField("Postal code"),
                NonNullObservableField("Postal region"),
                ObservableInt(1),
                ObservableInt(2),
                ObservableInt(3)
        )
        val parcel = Parcel.obtain()

        // When
        inputData.writeToParcel(parcel, inputData.describeContents())
        parcel.setDataPosition(0)
        val parsed = HouseholdInfo.CREATOR.createFromParcel(parcel)

        // Then
        assertThat(parsed).isEqualTo(inputData)
    }
}