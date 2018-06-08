package network;

import models.ResendModel;
import models.SignupModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/v1/registers")
    Call<SignupModel> userSignup(@Field("facebook_id") String facebook_id,
                                 @Field("email") String email,
                                 @Field("password") String password,
                                 @Field("linkedin_id") String linkedin_id,
                                 @Field("account_type") int account_type,
                                 @Field("device_token") String device_token,
                                 @Field("platform_type") int platform_type,
                                 @Field("email_verified") int email_verified,
                                 @Field("user_type") int user_type);

    @FormUrlEncoded
    @POST("/api/v1/authentications/authenticate")
    Call<SignupModel> userLogin(@Field("email") String email,
                                @Field("password") String password,
                                @Field("device_token") String device_token,
                                @Field("platform_type") int platform_type,
                                @Field("user_type") int user_type);

    @Multipart
    @POST("/api/v1/profiles/setup_profile")
    Call<SignupModel> createProfile(@Part("access_token") RequestBody access_token,
                                    @Part("avatar") RequestBody avatar,
                                    @Part("user_type") RequestBody user_type,
                                    @Part("full_name") RequestBody full_name,
                                    @Part("age") RequestBody age,
                                    @Part("gender") RequestBody gender,
                                    @Part("languages") RequestBody languages,
                                    @Part("profession_id") RequestBody profession_id,
                                    @Part("experience") RequestBody experience,
                                    @Part("skills") RequestBody skills,
                                    @Part("bio") RequestBody bio,
                                    @Part("pro_description") RequestBody pro_description,
                                    @Part MultipartBody.Part document);

    @FormUrlEncoded
    @POST("/api/v1/verifications/resend_email")
    Call<ResendModel> resendEmail(@Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/api/v1/verifications/send_email")
    Call<ResendModel> verifyEmail(@Field("email") String email,
                                  @Field("access_token") String access_token);


}