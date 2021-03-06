package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SignupModel extends BaseModel implements Parcelable {

    private ResponseBean response;
    private int code;
    private String message;
    private List<AvatarModel> avatars;
    private List<LanguageModel> languages;
    private List<ProfessionModel> professions;
    private List<SkillsModel> skills;
    private List<QuestionAnswerModel> answers;

    public List<QuestionAnswerModel> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerModel> answers) {
        this.answers = answers;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<AvatarModel> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<AvatarModel> avatars) {
        this.avatars = avatars;
    }

    public List<LanguageModel> getLanguages() {
        return languages;
    }

    public void setLanguages(List<LanguageModel> languages) {
        this.languages = languages;
    }

    public List<ProfessionModel> getProfessions() {
        return professions;
    }

    public void setProfessions(List<ProfessionModel> professions) {
        this.professions = professions;
    }

    public List<SkillsModel> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillsModel> skills) {
        this.skills = skills;
    }

    public static class ResponseBean implements Parcelable {
        /**
         * id : 8
         * full_name :
         * email : defectlifecycle@gmail.com
         * age :
         * gender :
         * access_token : 3048948249877b182526a71ab5859fc0
         * account_type : 2
         * facebook_id :
         * profile_status : 0
         * linkedin_id : BW5wX7-9h-
         * user_type : 0
         * email_verified : 1
         * skills : []
         * languages : []
         * avatar :
         * profession : {}
         * bio :
         * pro_description :
         * experience_year : 0
         * experience_month : 0
         * document_verified : 0
         * preferences : {"gender":0,"distance":0,"experience_year":0,"experience_month":0,"languages":[],"skills":[],"professions":[]}
         * answers : []
         */

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
        private AvatarBean avatar;
        private ProfessionBean profession;
        private String bio;
        private String pro_description;
        private int experience_year;
        private int experience_month;
        private int document_verified;
        private String tip;
        private PreferencesBean preferences;
        private List<String> skills;
        private List<LanguageModel> languages;
        private List<AnswerModel> answers;

        private int submitted_document;
        private int mentee_profile_status;
        private int mentee_question_status;
        private int mentor_profile_status;
        private int mentor_question_status;
        private int can_switch;
        private int switch_status;
        private int mentor_verified;
        private String availability;

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

        public String getAvailability() {
            return availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public AvatarBean getAvatar() {
            return avatar;
        }

        public void setAvatar(AvatarBean avatar) {
            this.avatar = avatar;
        }

        public ProfessionBean getProfession() {
            return profession;
        }

        public void setProfession(ProfessionBean profession) {
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

        public int getDocument_verified() {
            return document_verified;
        }

        public void setDocument_verified(int document_verified) {
            this.document_verified = document_verified;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public PreferencesBean getPreferences() {
            return preferences;
        }

        public void setPreferences(PreferencesBean preferences) {
            this.preferences = preferences;
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

        public List<AnswerModel> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswerModel> answers) {
            this.answers = answers;
        }

        public int getSubmitted_document() {
            return submitted_document;
        }

        public void setSubmitted_document(int submitted_document) {
            this.submitted_document = submitted_document;
        }

        public int getMentee_profile_status() {
            return mentee_profile_status;
        }

        public void setMentee_profile_status(int mentee_profile_status) {
            this.mentee_profile_status = mentee_profile_status;
        }

        public int getMentee_question_status() {
            return mentee_question_status;
        }

        public void setMentee_question_status(int mentee_question_status) {
            this.mentee_question_status = mentee_question_status;
        }

        public int getMentor_profile_status() {
            return mentor_profile_status;
        }

        public void setMentor_profile_status(int mentor_profile_status) {
            this.mentor_profile_status = mentor_profile_status;
        }

        public int getMentor_question_status() {
            return mentor_question_status;
        }

        public void setMentor_question_status(int mentor_question_status) {
            this.mentor_question_status = mentor_question_status;
        }

        public int getCan_switch() {
            return can_switch;
        }

        public void setCan_switch(int can_switch) {
            this.can_switch = can_switch;
        }

        public int getSwitch_status() {
            return switch_status;
        }

        public void setSwitch_status(int switch_status) {
            this.switch_status = switch_status;
        }

        public int getMentor_verified() {
            return mentor_verified;
        }

        public void setMentor_verified(int mentor_verified) {
            this.mentor_verified = mentor_verified;
        }

        public static class ProfessionBean implements Parcelable {

            private int id;
            private String name;
            private String created_at;
            private String updated_at;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCreated_at() {
                return created_at;
            }

            public void setCreated_at(String created_at) {
                this.created_at = created_at;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.name);
                dest.writeString(this.created_at);
                dest.writeString(this.updated_at);
            }

            public ProfessionBean() {
            }

            protected ProfessionBean(Parcel in) {
                this.id = in.readInt();
                this.name = in.readString();
                this.created_at = in.readString();
                this.updated_at = in.readString();
            }

            public static final Creator<ProfessionBean> CREATOR = new Creator<ProfessionBean>() {
                @Override
                public ProfessionBean createFromParcel(Parcel source) {
                    return new ProfessionBean(source);
                }

                @Override
                public ProfessionBean[] newArray(int size) {
                    return new ProfessionBean[size];
                }
            };
        }

        public static class AvatarBean implements Parcelable {
            private String avtar_url;
            private int gender;
            private int id;
            private String name;
            private int parent_id;

            public String getAvtar_url() {
                return avtar_url;
            }

            public void setAvtar_url(String avtar_url) {
                this.avtar_url = avtar_url;
            }

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getParent_id() {
                return parent_id;
            }

            public void setParent_id(int parent_id) {
                this.parent_id = parent_id;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.avtar_url);
                dest.writeInt(this.gender);
                dest.writeInt(this.id);
                dest.writeString(this.name);
                dest.writeInt(this.parent_id);
            }

            public AvatarBean() {
            }

            protected AvatarBean(Parcel in) {
                this.avtar_url = in.readString();
                this.gender = in.readInt();
                this.id = in.readInt();
                this.name = in.readString();
                this.parent_id = in.readInt();
            }

            public static final Creator<AvatarBean> CREATOR = new Creator<AvatarBean>() {
                @Override
                public AvatarBean createFromParcel(Parcel source) {
                    return new AvatarBean(source);
                }

                @Override
                public AvatarBean[] newArray(int size) {
                    return new AvatarBean[size];
                }
            };
        }


        public static class PreferencesBean implements Parcelable {
            /**
             * gender : 0
             * distance : 0
             * experience_year : 0
             * experience_month : 0
             * languages : []
             * skills : []
             * professions : []
             */

            private int gender;
            private int distance;
            private int experience_year;
            private int experience_month;
            private List<LanguageModel> languages;
            private List<SkillsModel> skills;
            private List<ProfessionModel> professions;

            public int getGender() {
                return gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public int getDistance() {
                return distance;
            }

            public void setDistance(int distance) {
                this.distance = distance;
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

            public List<LanguageModel> getLanguages() {
                return languages;
            }

            public void setLanguages(List<LanguageModel> languages) {
                this.languages = languages;
            }

            public List<SkillsModel> getSkills() {
                return skills;
            }

            public void setSkills(List<SkillsModel> skills) {
                this.skills = skills;
            }

            public List<ProfessionModel> getProfessions() {
                return professions;
            }

            public void setProfessions(List<ProfessionModel> professions) {
                this.professions = professions;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.gender);
                dest.writeInt(this.distance);
                dest.writeInt(this.experience_year);
                dest.writeInt(this.experience_month);
                dest.writeTypedList(this.languages);
                dest.writeTypedList(this.skills);
                dest.writeTypedList(this.professions);
            }

            public PreferencesBean() {
            }

            protected PreferencesBean(Parcel in) {
                this.gender = in.readInt();
                this.distance = in.readInt();
                this.experience_year = in.readInt();
                this.experience_month = in.readInt();
                this.languages = in.createTypedArrayList(LanguageModel.CREATOR);
                this.skills = in.createTypedArrayList(SkillsModel.CREATOR);
                this.professions = in.createTypedArrayList(ProfessionModel.CREATOR);
            }

            public static final Creator<PreferencesBean> CREATOR = new Creator<PreferencesBean>() {
                @Override
                public PreferencesBean createFromParcel(Parcel source) {
                    return new PreferencesBean(source);
                }

                @Override
                public PreferencesBean[] newArray(int size) {
                    return new PreferencesBean[size];
                }
            };
        }

        public ResponseBean() {
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
            dest.writeInt(this.experience_year);
            dest.writeInt(this.experience_month);
            dest.writeInt(this.document_verified);
            dest.writeString(this.tip);
            dest.writeParcelable(this.preferences, flags);
            dest.writeStringList(this.skills);
            dest.writeTypedList(this.languages);
            dest.writeList(this.answers);
            dest.writeInt(this.submitted_document);
            dest.writeInt(this.mentee_profile_status);
            dest.writeInt(this.mentee_question_status);
            dest.writeInt(this.mentor_profile_status);
            dest.writeInt(this.mentor_question_status);
            dest.writeInt(this.can_switch);
            dest.writeInt(this.switch_status);
            dest.writeInt(this.mentor_verified);
            dest.writeString(this.availability);
        }

        protected ResponseBean(Parcel in) {
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
            this.avatar = in.readParcelable(AvatarBean.class.getClassLoader());
            this.profession = in.readParcelable(ProfessionBean.class.getClassLoader());
            this.bio = in.readString();
            this.pro_description = in.readString();
            this.experience_year = in.readInt();
            this.experience_month = in.readInt();
            this.document_verified = in.readInt();
            this.tip = in.readString();
            this.preferences = in.readParcelable(PreferencesBean.class.getClassLoader());
            this.skills = in.createStringArrayList();
            this.languages = in.createTypedArrayList(LanguageModel.CREATOR);
            this.answers = new ArrayList<AnswerModel>();
            in.readList(this.answers, AnswerModel.class.getClassLoader());
            this.submitted_document = in.readInt();
            this.mentee_profile_status = in.readInt();
            this.mentee_question_status = in.readInt();
            this.mentor_profile_status = in.readInt();
            this.mentor_question_status = in.readInt();
            this.can_switch = in.readInt();
            this.switch_status = in.readInt();
            this.mentor_verified = in.readInt();
            this.availability = in.readString();
        }

        public static final Creator<ResponseBean> CREATOR = new Creator<ResponseBean>() {
            @Override
            public ResponseBean createFromParcel(Parcel source) {
                return new ResponseBean(source);
            }

            @Override
            public ResponseBean[] newArray(int size) {
                return new ResponseBean[size];
            }
        };
    }

    public SignupModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.response, flags);
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeTypedList(this.avatars);
        dest.writeTypedList(this.languages);
        dest.writeTypedList(this.professions);
        dest.writeTypedList(this.skills);
        dest.writeTypedList(this.answers);
    }

    protected SignupModel(Parcel in) {
        this.response = in.readParcelable(ResponseBean.class.getClassLoader());
        this.code = in.readInt();
        this.message = in.readString();
        this.avatars = in.createTypedArrayList(AvatarModel.CREATOR);
        this.languages = in.createTypedArrayList(LanguageModel.CREATOR);
        this.professions = in.createTypedArrayList(ProfessionModel.CREATOR);
        this.skills = in.createTypedArrayList(SkillsModel.CREATOR);
        this.answers = in.createTypedArrayList(QuestionAnswerModel.CREATOR);
    }

    public static final Creator<SignupModel> CREATOR = new Creator<SignupModel>() {
        @Override
        public SignupModel createFromParcel(Parcel source) {
            return new SignupModel(source);
        }

        @Override
        public SignupModel[] newArray(int size) {
            return new SignupModel[size];
        }
    };
}
