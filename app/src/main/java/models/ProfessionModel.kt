package models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by applify on 6/4/2018.
 */
data class ProfessionModel(var profession: String, var isSelected: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(profession)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfessionModel> {
        override fun createFromParcel(parcel: Parcel): ProfessionModel {
            return ProfessionModel(parcel)
        }

        override fun newArray(size: Int): Array<ProfessionModel?> {
            return arrayOfNulls(size)
        }
    }
}