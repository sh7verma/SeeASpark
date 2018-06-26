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
    private String avatar;
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
    private int post_type=0;
    private String title;
    private String description;
    private String date_time;
    private String url;
    private int is_featured;
    private List<ImageModel> images;


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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
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

    public List<ImageModel> getImages() {
        return images;
    }

    public void setImages(List<ImageModel> images) {
        this.images = images;
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
        dest.writeString(this.avatar);
        dest.writeParcelable(this.profession, flags);
        dest.writeString(this.bio);
        dest.writeString(this.pro_description);
        dest.writeInt(this.experience_year);
        dest.writeInt(this.experience_month);
        dest.writeStringList(this.skills);
        dest.writeTypedList(this.languages);
        dest.writeInt(this.admin_id);
        dest.writeInt(this.post_type);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.date_time);
        dest.writeString(this.url);
        dest.writeInt(this.is_featured);
        dest.writeList(this.images);
    }

    public CardsDisplayModel() {
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
        this.avatar = in.readString();
        this.profession = in.readParcelable(ProfessionModel.class.getClassLoader());
        this.bio = in.readString();
        this.pro_description = in.readString();
        this.experience_year = in.readInt();
        this.experience_month = in.readInt();
        this.skills = in.createStringArrayList();
        this.languages = in.createTypedArrayList(LanguageModel.CREATOR);
        this.admin_id = in.readInt();
        this.post_type = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.date_time = in.readString();
        this.url = in.readString();
        this.is_featured = in.readInt();
        this.images = new ArrayList<ImageModel>();
        in.readList(this.images, ImageModel.class.getClassLoader());
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
