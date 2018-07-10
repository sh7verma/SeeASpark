package network;

import models.CardModel;
import models.CommentModel;
import models.PostModel;
import models.ForgotPasswordModel;
import models.BaseSuccessModel;
import models.NotificationModel;
import models.SearchSkillModel;
import models.ServerSkillsModel;
import models.SignupModel;
import models.SwipeCardModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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
    Call<BaseSuccessModel> resendEmail(@Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/api/v1/verifications/send_email")
    Call<BaseSuccessModel> verifyEmail(@Field("email") String email,
                                       @Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/api/v1/preferences/update_preferences")
    Call<SignupModel> updatePreferences(@Field("access_token") String access_token,
                                        @Field("distance") int distance,
                                        @Field("experience") String experience,
                                        @Field("gender") int gender,
                                        @Field("skills") String skills,
                                        @Field("professions") String professions,
                                        @Field("languages") String languages);

    @FormUrlEncoded
    @POST("/api/v1/answers/post_answers")
    Call<SignupModel> postAnswers(@Field("access_token") String access_token,
                                  @Field("questions") String questions);


    @FormUrlEncoded
    @POST("/api/v1/skills/search_skill")
    Call<SearchSkillModel> searchSkills(@Field("access_token") String access_token,
                                        @Field("search") String search);


    @GET("/api/v1/forget_password")
    Call<ForgotPasswordModel> forgotPassword(@Query("email") String email);

    @GET("/api/v1/cards")
    Call<CardModel> getCards(@Query("access_token") String access_token,
                             @Query("latitude") String latitude,
                             @Query("longitude") String longitude,
                             @Query("page") String page);

    @FormUrlEncoded
    @POST("/api/v1/cards/swipe")
    Call<SwipeCardModel> swipeCards(@Field("access_token") String access_token,
                                    @Field("swipe_type") int swipe_type,
                                    @Field("other_user_id") String other_user_id);


    @GET("/api/v1/delete_account")
    Call<BaseSuccessModel> deleteAccount(@Query("access_token") String access_token);

    @GET("/api/v1/deactivate_account")
    Call<BaseSuccessModel> deactivateAccount(@Query("access_token") String access_token);

    @GET("/api/v1/skip_tip")
    Call<BaseSuccessModel> skipTip(@Query("access_token") String access_token);


    @FormUrlEncoded
    @POST("/api/v1/profiles/edit_profile")
    Call<SignupModel> editProfile(@Field("access_token") String access_token,
                                  @Field("avatar") String avatar,
                                  @Field("full_name") String full_name,
                                  @Field("age") String age,
                                  @Field("gender") String gender,
                                  @Field("languages") String languages,
                                  @Field("profession_id") String profession_id,
                                  @Field("experience") String experience,
                                  @Field("skills") String skills,
                                  @Field("bio") String bio,
                                  @Field("pro_description") String pro_description);

    @FormUrlEncoded
    @POST("/api/v1/settings/change_password")
    Call<BaseSuccessModel> changePassword(@Field("access_token") String access_token,
                                          @Field("old_password") String old_password,
                                          @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/v1/settings/update_notifications")
    Call<BaseSuccessModel> updateNotifications(@Field("access_token") String access_token,
                                               @Field("messages_notification") Integer messages_notification,
                                               @Field("posts_notification") Integer posts_notification,
                                               @Field("qoutes_notification") Integer qoutes_notification,
                                               @Field("notes_notification") Integer notes_notification);

    @GET("/api/v1/notification_settings")
    Call<NotificationModel> notificationSettings(@Query("access_token") String access_token);


    @GET("/api/v1/user_skills")
    Call<ServerSkillsModel> getUserSkills(@Query("access_token") String access_token);


    @GET("/api/v1/posts")
    Call<PostModel> getPosts(@Query("access_token") String access_token,
                             @Query("post_type") String post_type,
                             @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/v1/activities")
    Call<BaseSuccessModel> eventActivity(@Field("access_token") String access_token,
                                         @Field("post_id") int post_id,
                                         @Field("activity_type") int activity_type);

    @FormUrlEncoded
    @POST("/api/v1/bookmarks")
    Call<BaseSuccessModel> eventBookmark(@Field("access_token") String access_token,
                                         @Field("post_id") int post_id);

    @GET("/api/v1/comments")
    Call<CommentModel> getComments(@Query("access_token") String access_token,
                                   @Query("post_id") int post_id,
                                   @Query("page") int page);


}