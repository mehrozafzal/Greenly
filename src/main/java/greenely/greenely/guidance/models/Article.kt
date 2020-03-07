package greenely.greenely.guidance.models

import android.os.Parcel
import android.os.Parcelable

data class Article(
        val thumbnailImageUrl: String,
        val thumbnailTitle: String,
        val imageUrl: String,
        val title: String,
        val text: String,
        val link: String?,
        val label: String,
        val id: Int,
        val linkText: String?) : Parcelable {

    fun hasLink(): Boolean = !link.isNullOrEmpty() && !linkText.isNullOrEmpty()

    constructor(parcel: Parcel) : this(
            thumbnailImageUrl = parcel.readString(),
            thumbnailTitle = parcel.readString(),
            imageUrl = parcel.readString(),
            title = parcel.readString(),
            text = parcel.readString(),
            link = parcel.readString(),
            label = parcel.readString(),
            id = parcel.readInt(),
            linkText = parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(thumbnailImageUrl)
        parcel.writeString(thumbnailTitle)
        parcel.writeString(imageUrl)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeString(link)
        parcel.writeString(label)
        parcel.writeInt(id)
        parcel.writeString(linkText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }
}