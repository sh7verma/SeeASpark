package network;

import models.CardModel;
import models.CommentModel;
import models.LanguageListingModel;
import models.MessageHistoryModel;
import models.NotesListingModel;
import models.NotesModel;
import models.OtherProfileModel;
import models.PaymentAdditionModel;
import models.PaymentsHistoryModel;
import models.PlansModel;
import models.PostDetailModel;
import models.PostModel;
import models.ForgotPasswordModel;
import models.BaseSuccessModel;
import models.NotificationModel;
import models.ProfessionListingModel;
import models.QuestionListingModel;
import models.RatingModel;
import models.SearchSkillModel;
import models.ServerSkillsModel;
import models.SignupModel;
import models.SwipeCardModel;
import models.ViewProfileModel;
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
                                    @Part("profession") RequestBody profession,
                                    @Part("experience") RequestBody experience,
                                    @Part("skills") RequestBody skills,
                                    @Part("bio") RequestBody bio,
                                    @Part("pro_description") RequestBody pro_description,
                                    @Part MultipartBody.Part document);


    @Multipart
    @POST("/api/v1/switches/upload_document")
    Call<SignupModel> switchUploadDocument(@Part("access_token") RequestBody access_token,
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
                                  @Field("profession") String profession,
                                  @Field("experience") String experience,
                                  @Field("skills") String skills,
                                  @Field("bio") String bio,
                                  @Field("pro_description") String pro_description,
                                  @Field("availability") String availability);

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
    Call<BaseSuccessModel> postActivity(@Field("access_token") String access_token,
                                        @Field("post_id") int post_id,
                                        @Field("activity_type") int activity_type);

    @FormUrlEncoded
    @POST("/api/v1/bookmarks")
    Call<BaseSuccessModel> markBookmark(@Field("access_token") String access_token,
                                        @Field("post_id") int post_id);

    @GET("/api/v1/comments")
    Call<CommentModel> getComments(@Query("access_token") String access_token,
                                   @Query("post_id") int post_id,
                                   @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/v1/comments")
    Call<BaseSuccessModel> postComments(@Field("access_token") String access_token,
                                        @Field("post_id") int post_id,
                                        @Field("description") String description);

    @FormUrlEncoded
    @POST("/api/v1/comments/latest_comments")
    Call<CommentModel> getLatestCommnets(@Field("access_token") String access_token,
                                         @Field("post_id") int post_id,
                                         @Field("last_id") int last_id);


    @FormUrlEncoded
    @POST("/api/v1/suggestions")
    Call<BaseSuccessModel> shareAnIdea(@Field("access_token") String access_token,
                                       @Field("title") String title,
                                       @Field("description") String description,
                                       @Field("post_type") int post_type);


    @GET("/api/v1/bookmarks")
    Call<PostModel> getBookmarkPosts(@Query("access_token") String access_token,
                                     @Query("page") int page,
                                     @Query("post_type") int post_type);

    @FormUrlEncoded
    @POST("/api/v1/posts/search_post")
    Call<PostModel> searchPost(@Field("access_token") String access_token,
                               @Field("post_type") int post_type,
                               @Field("query") String query,
                               @Field("page") int page);

    @FormUrlEncoded
    @POST("/api/v1/notes/search")
    Call<NotesListingModel> searchNotes(@Field("access_token") String access_token,
                                        @Field("note_type") String note_type,
                                        @Field("search") String search,
                                        @Field("page") String page);

    @FormUrlEncoded
    @POST("/api/v1/bookmarks/search")
    Call<PostModel> searchBookmarkPost(@Field("access_token") String access_token,
                                       @Field("post_type") int post_type,
                                       @Field("query") String query,
                                       @Field("page") int page);


    @GET("/api/v1/professions")
    Call<ProfessionListingModel> getProfessions(@Query("access_token") String access_token);

    @GET("/api/v1/languages")
    Call<LanguageListingModel> getLanguages(@Query("access_token") String access_token);


    @FormUrlEncoded
    @POST("/api/v1/professions/search")
    Call<ProfessionListingModel> searchProfessions(@Field("access_token") String access_token,
                                                   @Field("search") String search);

    @FormUrlEncoded
    @POST("/api/v1/notes")
    Call<NotesModel> addNotes(@Field("access_token") String access_token,
                              @Field("title") String title,
                              @Field("note_title") String note_title,
                              @Field("description") String description);

    @FormUrlEncoded
    @POST("/api/v1/notes/update_note")
    Call<NotesModel> updateNotes(@Field("access_token") String access_token,
                                 @Field("id") String id,
                                 @Field("title") String title,
                                 @Field("description") String description);

    @GET("/api/v1/notes")
    Call<NotesListingModel> getNotes(@Query("access_token") String access_token,
                                     @Query("note_type") String note_type,
                                     @Query("page") String page);


    @FormUrlEncoded
    @POST("/api/v1/notes/delete_note")
    Call<BaseSuccessModel> deleteNote(@Field("access_token") String access_token,
                                      @Field("id") String id);

    @FormUrlEncoded
    @POST("/api/v1/shares/shared_note")
    Call<NotesModel> fetchDetailNotes(@Field("access_token") String access_token,
                                      @Field("id") String id,
                                      @Field("name") String file_name);


    @FormUrlEncoded
    @POST("/api/v1/shares/external_share")
    Call<NotesModel> shareNote(@Field("access_token") String access_token,
                               @Field("id") String id,
                               @Field("name") String file_name);

    @FormUrlEncoded
    @POST("/api/v1/shares/delete_share")
    Call<BaseSuccessModel> deleteReceivedNote(@Field("access_token") String access_token,
                                              @Field("id") String id,
                                              @Field("name") String name);

    @FormUrlEncoded
    @POST("/api/v1/posts/post_by_id")
    Call<PostDetailModel> getPostDetail(@Field("access_token") String access_token,
                                        @Field("id") int id);

    @FormUrlEncoded
    @POST("/api/v1/switches/questions")
    Call<QuestionListingModel> getSwitchQuestions(@Field("access_token") String access_token,
                                                  @Field("user_type") int user_type);

    @FormUrlEncoded
    @POST("/api/v1/switches/post_answers")
    Call<SignupModel> postSwitchAnswers(@Field("access_token") String access_token,
                                        @Field("user_type") int user_type,
                                        @Field("questions") String questions);


    @FormUrlEncoded
    @POST("/api/v1/switches/switch")
    Call<SignupModel> switchUser(@Field("access_token") String access_token,
                                 @Field("user_type") int user_type);


    @GET("/api/v1/users")
    Call<ViewProfileModel> getProfile(@Query("access_token") String access_token,
                                      @Query("id") String id);

    @GET("/api/v1/users")
    Call<OtherProfileModel> getOtherProfile(@Query("access_token") String access_token,
                                            @Query("id") String id);

    @FormUrlEncoded
    @POST("/api/v1/switches/submit_profile")
    Call<SignupModel> submitProfile(@Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("/api/v1/shares/share")
    Call<NotesModel> shareNotes(@Field("access_token") String access_token,
                                @Field("id") String id,
                                @Field("receiver_id") String receiver_id,
                                @Field("name") String file_name);

    @FormUrlEncoded
    @POST("/api/v1/ratings")
    Call<BaseSuccessModel> rateUser(@Field("access_token") String access_token,
                                    @Field("other_user_id") String other_user_id,
                                    @Field("rating") String rating,
                                    @Field("comment") String comment);

    @GET("/api/v1/ratings")
    Call<RatingModel> getRating(@Query("access_token") String access_token,
                                @Query("other_user_id") String other_user_id);

    @FormUrlEncoded
    @POST("/api/v1/matches/unmatch")
    Call<BaseSuccessModel> unmatch(@Field("access_token") String access_token,
                                   @Field("other_user_id") String other_user_id);

    @FormUrlEncoded
    @POST("/api/v1/chats/mark_as_favourite")
    Call<BaseSuccessModel> favouriteMessage(@Field("access_token") String access_token,
                                            @Field("message_id") String message_id);

    @FormUrlEncoded
    @POST("/api/v1/chats/delete_messages")
    Call<BaseSuccessModel> deleteMessage(@Field("access_token") String access_token,
                                         @Field("message_id") String message_id);

    @FormUrlEncoded
    @POST("/api/v1/chats/chat_history")
    Call<MessageHistoryModel> getChatHistory(@Field("access_token") String access_token);

    @GET("/api/v1/plans")
    Call<PlansModel> getPlans(@Query("access_token") String access_token,
                              @Query("plan_type") String plan_type);

    @FormUrlEncoded
    @POST("/api/v1/subscription/plan_subscription")
    Call<PaymentAdditionModel> addPlanSubscription(@Field("access_token") String access_token,
                                                   @Field("payment_status") String payment_status,
                                                   @Field("plan_id") String plan_id,
                                                   @Field("payment_response") String payment_response,
                                                   @Field("payment_type") String payment_type,
                                                   @Field("purchase_date") String purchase_time,
                                                   @Field("transaction_id") String transaction_id);


    @FormUrlEncoded
    @POST("/api/v1/subscription/subscription_histroy")
    Call<PaymentsHistoryModel> getPaymentHistory(@Field("access_token") String access_token);


}