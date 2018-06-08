package models

import com.google.gson.annotations.SerializedName

data class PreferencesItem(@SerializedName("gender")
                           val gender: Int = 0,
                           @SerializedName("distance")
                           val distance: Int = 0,
                           @SerializedName("experience_year")
                           val experienceYear: Int = 0,
                           @SerializedName("experience_month")
                           val experienceMonth: Int = 0)