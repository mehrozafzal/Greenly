package greenely.greenely.solaranalysis.models

import android.os.Parcel
import android.os.Parcelable

data class Analysis(
        val id: String,
        val totalSaving: String,
        val estimatedCostAfterSolarSupport: String,
        val yearlySaving: String,
        val yearlyProduction: String,
        val potentialSaving: String,
        val paybackTimeWithSolarSupport: String,
        val solarPanelLifeSpan: String,
        private val _monthData: MutableList<Float> = mutableListOf()
) : Parcelable {

    val monthData: List<Float> = _monthData

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        val listSize = parcel.readInt()
        val array = FloatArray(listSize)
        parcel.readFloatArray(array)
        _monthData.addAll(array.toList())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(totalSaving)
        parcel.writeString(estimatedCostAfterSolarSupport)
        parcel.writeString(yearlySaving)
        parcel.writeString(yearlyProduction)
        parcel.writeString(potentialSaving)
        parcel.writeString(paybackTimeWithSolarSupport)
        parcel.writeString(solarPanelLifeSpan)
        parcel.writeInt(monthData.size)
        parcel.writeFloatArray(_monthData.toFloatArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Analysis> {
        override fun createFromParcel(parcel: Parcel): Analysis {
            return Analysis(parcel)
        }

        override fun newArray(size: Int): Array<Analysis?> {
            return arrayOfNulls(size)
        }
    }
}
