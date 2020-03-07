package greenely.greenely.solaranalysis.models

import android.os.Parcel
import android.os.Parcelable
import greenely.greenely.utils.NonNullObservableField

class ContactInfo(
        val name: NonNullObservableField<String> = NonNullObservableField(""),
        val email: NonNullObservableField<String> = NonNullObservableField(""),
        val phoneNumber: NonNullObservableField<String> = NonNullObservableField(""),
        val qualityLead: NonNullObservableField<Boolean> = NonNullObservableField(false)
) : Parcelable {
    constructor(parcel: Parcel) : this(
            NonNullObservableField(parcel.readString()),
            NonNullObservableField(parcel.readString()),
            NonNullObservableField(parcel.readString()),
            NonNullObservableField(parcel.readByte() != 0.toByte()))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactInfo

        if (name.get() != other.name.get()) return false
        if (email.get() != other.email.get()) return false
        if (phoneNumber.get() != other.phoneNumber.get()) return false
        if (qualityLead.get() != other.qualityLead.get()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + qualityLead.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name.get())
        parcel.writeString(email.get())
        parcel.writeString(phoneNumber.get())
        parcel.writeByte(if (qualityLead.get()) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactInfo> {
        override fun createFromParcel(parcel: Parcel): ContactInfo {
            return ContactInfo(parcel)
        }

        override fun newArray(size: Int): Array<ContactInfo?> {
            return arrayOfNulls(size)
        }
    }
}

