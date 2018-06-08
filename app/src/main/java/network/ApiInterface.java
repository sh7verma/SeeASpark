package network;

import models.SignupModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}