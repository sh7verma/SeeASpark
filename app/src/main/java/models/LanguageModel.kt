package models

import android.os.Parcel
import android.os.Parcelable


data class LanguageModel(var language: String, var isSelected: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(language)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LanguageModel> {
        override fun createFromParcel(parcel: Parcel): LanguageModel {
            return LanguageModel(parcel)
        }

        override fun newArray(size: Int): Array<LanguageModel?> {
            return arrayOfNulls(size)
        }
    }
}