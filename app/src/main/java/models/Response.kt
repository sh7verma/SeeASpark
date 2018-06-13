package models

import com.google.gson.annotations.SerializedName

data class Response(@SerializedName("profession")
                    val profession: String = "",
                    @SerializedName("account_type")
                    val accountType: Int = 0,
                    @SerializedName("preferences")
                    val preferences: List<PreferencesItem>?,
                    @SerializedName("email_verified")
                    val emailVerified: Int = 0,
                    @SerializedName("gender")
                    val gender: String = "",
                    @SerializedName("linkedin_id")
                    val linkedinId: String = "",
                    @SerializedName("bio")
                    val bio: String = "",
                    @SerializedName("avatar")
                    val avatar: String = "",
                    @SerializedName("experience_year")
                    val experienceYear: Int = 0,
                    @SerializedName("document_verified")
                    val documentVerified: Int = 0,
                    @SerializedName("facebook_id")
                    val facebookId: String = "",
                    @SerializedName("experience_month")
                    val experienceMonth: Int = 0,
                    @SerializedName("access_token")
                    val accessToken: String = "",
                    @SerializedName("profile_status")
                    val profileStatus: Int = 0,
                    @SerializedName("full_name")
                    val fullName: String = "",
                    @SerializedName("user_type")
                    val userType: Int = 0,
                    @SerializedName("pro_description")
                    val proDescription: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("email")
                    val email: String = "",
                    @SerializedName("age")
                    val age: String = "")