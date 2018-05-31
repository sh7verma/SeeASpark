package models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by applify on 5/30/2018.
 */
data class SkillsModel(var skill: String, var isSelected: Boolean,var isFirstElement:Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(skill)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeByte(if (isFirstElement) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SkillsModel> {
        override fun createFromParcel(parcel: Parcel): SkillsModel {
            return SkillsModel(parcel)
        }

        override fun newArray(size: Int): Array<SkillsModel?> {
            return arrayOfNulls(size)
        }
    }

}