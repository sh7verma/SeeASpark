package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CardsDisplayModel implements Parcelable {

    private int id;
    private String full_name;
    private String email;
    private String age;
    private String gender;
    private String access_token;
    private int account_type;
    private String facebook_id;
    private int profile_status;
    private String linkedin_id;
    private int user_type;
    private int email_verified;
    private SignupModel.ResponseBean.AvatarBean avatar;
    private ProfessionModel profession;
    private String bio;
    private String pro_description;
    private String time_left;
    private int experience_year;
    private int experience_month;
    private List<String> skills;
    private List<LanguageModel> languages;


    /// post parameters
    private int admin_id;
    private int post_type = 0;
    private String title;
    private String description;
    private String date_time;
    private String url;
    private int is_featured;
    private String profession_id;
    private int like;
    private int comment;
    private int going;
    private int interested;
    private int liked;
    private int is_going;
    private int bookmarked;
    private String address;
    private String latitude;
    private String longitude;
    private String shareable_link;
    private List<PostModel.ResponseBean.GoingUserBean> going_list;
    private List<PostModel.ResponseBean.ImagesBean> images;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(String profession_id) {
        this.profession_id = profession_id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getGoing() {
        return going;
    }

    public void setGoing(int going) {
        this.going = going;
    }

    public int getInterested() {
        return interested;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public int getIs_going() {
        return is_going;
    }

    public void setIs_going(int is_going) {
        this.is_going = is_going;
    }

    public int getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(int bookmarked) {
        this.bookmarked = bookmarked;
    }

    public List<PostModel.ResponseBean.GoingUserBean> getGoing_list() {
        return going_list;
    }

    public void setGoing_list(List<PostModel.ResponseBean.GoingUserBean> going_list) {
        this.going_list = going_list;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public int getProfile_status() {
        return profile_status;
    }

    public void setProfile_status(int profile_status) {
        this.profile_status = profile_status;
    }

    public String getLinkedin_id() {
        return linkedin_id;
    }

    public void setLinkedin_id(String linkedin_id) {
        this.linkedin_id = linkedin_id;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public int getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(int email_verified) {
        this.email_verified = email_verified;
    }

    public SignupModel.ResponseBean.AvatarBean getAvatar() {
        return avatar;
    }

    public void setAvatar(SignupModel.ResponseBean.AvatarBean avatar) {
        this.avatar = avatar;
    }

    public ProfessionModel getProfession() {
        return profession;
    }

    public void setProfession(ProfessionModel profession) {
        this.profession = profession;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPro_description() {
        return pro_description;
    }

    public void setPro_description(String pro_description) {
        this.pro_description = pro_description;
    }

    public String getShareable_link() {
        return shareable_link;
    }

    public void setShareable_link(String shareable_link) {
        this.shareable_link = shareable_link;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public int getExperience_year() {
        return experience_year;
    }

    public void setExperience_year(int experience_year) {
        this.experience_year = experience_year;
    }

    public int getExperience_month() {
        return experience_month;
    }

    public void setExperience_month(int experience_month) {
        this.experience_month = experience_month;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<LanguageModel> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageModel> languages) {
        this.languages = languages;
    }

    //////

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIs_featured() {
        return is_featured;
    }

    public void setIs_featured(int is_featured) {
        this.is_featured = is_featured;
    }

    public List<PostModel.ResponseBean.ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<PostModel.ResponseBean.ImagesBean> images) {
        this.images = images;
    }

    public CardsDisplayModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.full_name);
        dest.writeString(this.email);
        dest.writeString(this.age);
        dest.writeString(this.gender);
        dest.writeString(this.access_token);
        dest.writeInt(this.account_type);
        dest.writeString(this.facebook_id);
        dest.writeInt(this.profile_status);
        dest.writeString(this.linkedin_id);
        dest.writeInt(this.user_type);
        dest.writeInt(this.email_verified);
        dest.writeParcelable(this.avatar, flags);
        dest.writeParcelable(this.profession, flags);
        dest.writeString(this.bio);
        dest.writeString(this.pro_description);
        dest.writeString(this.time_left);
        dest.writeInt(this.experience_year);
        dest.writeInt(this.experience_month);
        dest.writeString(this.shareable_link);
        dest.writeStringList(this.skills);
        dest.writeTypedList(this.languages);
        dest.writeInt(this.admin_id);
        dest.writeInt(this.post_type);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.date_time);
        dest.writeString(this.url);
        dest.writeInt(this.is_featured);
        dest.writeString(this.profession_id);
        dest.writeInt(this.like);
        dest.writeInt(this.comment);
        dest.writeInt(this.going);
        dest.writeInt(this.interested);
        dest.writeInt(this.liked);
        dest.writeInt(this.is_going);
        dest.writeInt(this.bookmarked);
        dest.writeString(this.address);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeTypedList(this.going_list);
        dest.writeTypedList(this.images);
    }

    protected CardsDisplayModel(Parcel in) {
        this.id = in.readInt();
        this.full_name = in.readString();
        this.email = in.readString();
        this.age = in.readString();
        this.gender = in.readString();
        this.access_token = in.readString();
        this.account_type = in.readInt();
        this.facebook_id = in.readString();
        this.profile_status = in.readInt();
        this.linkedin_id = in.readString();
        this.user_type = in.readInt();
        this.email_verified = in.readInt();
        this.avatar = in.readParcelable(SignupModel.ResponseBean.AvatarBean.class.getClassLoader());
        this.profession = in.readParcelable(ProfessionModel.class.getClassLoader());
        this.bio = in.readString();
        this.pro_description = in.readString();
        this.time_left = in.readString();
        this.experience_year = in.readInt();
        this.experience_month = in.readInt();
        this.shareable_link = in.readString();
        this.skills = in.createStringArrayList();
        this.languages = in.createTypedArrayList(LanguageModel.CREATOR);
        this.admin_id = in.readInt();
        this.post_type = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.date_time = in.readString();
        this.url = in.readString();
        this.is_featured = in.readInt();
        this.profession_id = in.readString();
        this.like = in.readInt();
        this.comment = in.readInt();
        this.going = in.readInt();
        this.interested = in.readInt();
        this.liked = in.readInt();
        this.is_going = in.readInt();
        this.bookmarked = in.readInt();
        this.address = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.going_list = in.createTypedArrayList(PostModel.ResponseBean.GoingUserBean.CREATOR);
        this.images = in.createTypedArrayList(PostModel.ResponseBean.ImagesBean.CREATOR);
    }

    public static final Creator<CardsDisplayModel> CREATOR = new Creator<CardsDisplayModel>() {
        @Override
        public CardsDisplayModel createFromParcel(Parcel source) {
            return new CardsDisplayModel(source);
        }

        @Override
        public CardsDisplayModel[] newArray(int size) {
            return new CardsDisplayModel[size];
        }
    };
}
