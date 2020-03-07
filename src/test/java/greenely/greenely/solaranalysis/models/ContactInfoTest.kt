package greenely.greenely.solaranalysis.models

import android.os.Parcel
import greenely.greenely.utils.NonNullObservableField
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
class ContactInfoTest {
    @Test
    fun testParcel() {
        val contactInfo = ContactInfo(
                NonNullObservableField("Name"),
                NonNullObservableField("Email"),
                NonNullObservableField("070 000 00 00")
        )
        val parcel = Parcel.obtain()

        // When
        contactInfo.writeToParcel(parcel, contactInfo.describeContents())
        parcel.setDataPosition(0)
        val parsed = ContactInfo.CREATOR.createFromParcel(parcel)

        // Then
        assertThat(parsed).isEqualTo(contactInfo)
    }
}