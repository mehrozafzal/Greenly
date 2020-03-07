package greenely.greenely.solaranalysis.models

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import android.os.Parcel
import android.os.Parcelable
import greenely.greenely.utils.NonNullObservableField

class HouseholdInfo(
        val address: NonNullObservableField<String> = NonNullObservableField(""),
        val postalCode: NonNullObservableField<String> = NonNullObservableField(""),
        val postalRegion: ObservableField<String> = ObservableField(""),
        val roofSizeId: ObservableInt = ObservableInt(-1),
        val roofAngleId: ObservableInt = ObservableInt(-1),
        val roofDirectionId: ObservableInt = ObservableInt(-1)
) : Parcelable {
    constructor(parcel: Parcel) : this(
            NonNullObservableField(parcel.readString()),
            NonNullObservableField(parcel.readString()),
            ObservableField(parcel.readString()),
            ObservableInt(parcel.readInt()),
            ObservableInt(parcel.readInt()),
            ObservableInt(parcel.readInt()))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HouseholdInfo

        if (address.get() != other.address.get()) return false
        if (postalCode.get() != other.postalCode.get()) return false
        if (postalRegion.get() != other.postalRegion.get()) return false
        if (roofSizeId.get() != other.roofSizeId.get()) return false
        if (roofAngleId.get() != other.roofAngleId.get()) return false
        if (roofDirectionId.get() != other.roofDirectionId.get()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + postalCode.hashCode()
        result = 31 * result + postalRegion.hashCode()
        result = 31 * result + roofSizeId.hashCode()
        result = 31 * result + roofAngleId.hashCode()
        result = 31 * result + roofDirectionId.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address.get())
        parcel.writeString(postalCode.get())
        parcel.writeString(postalRegion.get())
        parcel.writeInt(roofSizeId.get())
        parcel.writeInt(roofAngleId.get())
        parcel.writeInt(roofDirectionId.get())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HouseholdInfo> {
        override fun createFromParcel(parcel: Parcel): HouseholdInfo {
            return HouseholdInfo(parcel)
        }

        override fun newArray(size: Int): Array<HouseholdInfo?> {
            return arrayOfNulls(size)
        }
    }

}

