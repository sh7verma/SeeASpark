package models;

import java.util.List;

public class CardModel extends BaseModel {

    /**
     * response : [{"id":17,"full_name":"Sonal Malik","email":"sonal.m@applify.co","age":"20-06-2004","gender":"1","access_token":"b2e651cbaaede4884629ea7a0c80a95d","account_type":1,"facebook_id":"595482177503139","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_11.png","profession":{"id":1,"name":"Actor","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Raja","pro_description":"Ycycyc","experience_year":0,"experience_month":0},{"id":19,"full_name":"Bashayer","email":"bmaljaber@Gmail.com","age":"12-06-2000","gender":"2","access_token":"db0daf0139a131a9df4158c2350eb571","account_type":0,"facebook_id":"","profile_status":2,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Self-motivation ","Adaptability ","Conflict Resolution","Teamwork","Decision Making","Time Management"],"languages":[{"id":6,"name":"Arabic"},{"id":1,"name":"English"},{"id":23,"name":"German"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_15.png","profession":{"id":4,"name":"Consultant","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Short bio","pro_description":"Profession ","experience_year":5,"experience_month":3},{"id":36,"full_name":"abc test","email":"sonalapplify1@gmail.com","age":"13-06-2004","gender":"1","access_token":"ced9c5470f43418422046be7397d19c1","account_type":2,"facebook_id":"","profile_status":1,"linkedin_id":"dg-ULTmdJR","user_type":0,"email_verified":1,"skills":["Adaptability ","Ability to Work Under Pressure","Decision Making","Leadership","Self-motivation ","Buubib","Vubub"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_5.png","profession":{"id":1,"name":"Actor","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Yui","pro_description":"Bnj","experience_year":0,"experience_month":0},{"id":51,"full_name":"Test","email":"gurpreet.s+3@applify.co","age":"14-06-2004","gender":"1","access_token":"3bfa78f3529e40e55d9b32d3f987aa86","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_1.png","profession":{"id":3,"name":"Accountant","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Test","pro_description":"Test","experience_year":0,"experience_month":0},{"id":52,"full_name":"Tester","email":"gurpreet.s+4@applify.co","age":"14-06-2004","gender":"1","access_token":"4868e7c2a25bd14ff123657dad9d7ea7","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_1.png","profession":{"id":6,"name":"Dentist","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Test","pro_description":"Test","experience_year":0,"experience_month":0},{"id":57,"full_name":"Hdndjd","email":"gurpreet.s+11@applify.co","age":"14-06-2004","gender":"1","access_token":"4a57b0138c9a08507440eb1df09b09d1","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_3.png","profession":{"id":2,"name":"Architecture","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Chchhc","pro_description":"Chch","experience_year":0,"experience_month":0},{"id":58,"full_name":"Vinod","email":"gurpreet.s+12@applify.co","age":"14-06-2002","gender":"2","access_token":"dfe74826f3a31e5c9548cb845d3c6c78","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_14.png","profession":{"id":7,"name":"Engineer","created_at":"2018-06-01T05:57:39.000Z","updated_at":"2018-06-01T05:57:39.000Z"},"bio":"Test","pro_description":"Hs","experience_year":2,"experience_month":1},{"id":82,"full_name":"Bash ","email":"bmaljaber+1@gmail.com","age":"18-07-1995","gender":"2","access_token":"4b425e549abe5bcefbf054280475f692","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Time Management","Decision Making","Conflict Resolution","Teamwork"],"languages":[{"id":6,"name":"Arabic"},{"id":5,"name":"Amharic"},{"id":3,"name":"Abkhazian"},{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_1.png","profession":{"id":4,"name":"Consultant","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Baghdad ","pro_description":"Gh","experience_year":7,"experience_month":3},{"id":83,"full_name":"Viriato","email":"viriatovb@hotmail.com","age":"09-06-1990","gender":"1","access_token":"500e1d915981b574dea29b52cfd0184d","account_type":0,"facebook_id":"","profile_status":1,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure","Self-motivation ","Adaptability ","Time Management","Conflict Resolutionzb","Pizza","Potatoes"],"languages":[{"id":1,"name":"English"},{"id":27,"name":"Spanish"},{"id":34,"name":"French"},{"id":89,"name":"Portuguese"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_3.png","profession":{"id":2,"name":"Architecture","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Testing my patience","pro_description":"Spontaneous combustion","experience_year":0,"experience_month":0},{"id":84,"full_name":"Rajat Arora","email":"rajatarora028@gmail.com","age":"19-06-2004","gender":"1","access_token":"b97163e3a2ea37bbc0e9ff28930a802c","account_type":1,"facebook_id":"1898182183578587","profile_status":2,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":4,"name":"Afrikaans"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","profession":{"id":3,"name":"Accountant","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Test","pro_description":"Cvv","experience_year":1,"experience_month":1},{"id":85,"full_name":"Nancym","email":"Nancy.m+900@applify.co","age":"20-06-2004","gender":"3","access_token":"95b810494db2b6b2f8172f3ccbdc5d4d","account_type":0,"facebook_id":"","profile_status":2,"linkedin_id":"","user_type":0,"email_verified":1,"skills":["Ability to Work Under Pressure","Self-motivation ","Adaptability "],"languages":[{"id":2,"name":"Afar"},{"id":3,"name":"Abkhazian"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_5.png","profession":{"id":2,"name":"Architecture","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Jdnmxncmmxncm","pro_description":"Bznznnxnxnxmxmmxnxmncncncn","experience_year":3,"experience_month":0}]
     * posts : [{"id":2,"admin_id":1,"post_type":2,"title":"Lorem Ipsum Event","description":"Lorem Ipsum Article Event","date_time":"2018-06-19 11:25","url":"http://www.google.com","is_featured":1,"images":[{"id":5,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_5.png"},{"id":6,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_6.png"},{"id":7,"image_url":"h     ttps://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_7.png"}]}]
     * code : 111
     */

    private int code;
    private String time_left;
    private List<CardsDisplayModel> response;
    private List<CardsDisplayModel> posts;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<CardsDisplayModel> getResponse() {
        return response;
    }

    public void setResponse(List<CardsDisplayModel> response) {
        this.response = response;
    }

    public List<CardsDisplayModel> getPosts() {
        return posts;
    }

    public void setPosts(List<CardsDisplayModel> posts) {
        this.posts = posts;
    }

    public String getTime_left() {
        return time_left;
    }

    public void setTime_left(String time_left) {
        this.time_left = time_left;
    }

    public static class ResponseBean {
        /**
         * id : 17
         * full_name : Sonal Malik
         * email : sonal.m@applify.co
         * age : 20-06-2004
         * gender : 1
         * access_token : b2e651cbaaede4884629ea7a0c80a95d
         * account_type : 1
         * facebook_id : 595482177503139
         * profile_status : 1
         * linkedin_id :
         * user_type : 0
         * email_verified : 1
         * skills : ["Ability to Work Under Pressure"]
         * languages : [{"id":1,"name":"English"}]
         * avatar : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_11.png
         * profession : {"id":1,"name":"Actor","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"}
         * bio : Raja
         * pro_description : Ycycyc
         * experience_year : 0
         * experience_month : 0
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
        private String avatar;
        private ProfessionModel profession;
        private String bio;
        private String pro_description;
        private int experience_year;
        private int experience_month;
        private List<String> skills;
        private List<LanguagesBean> languages;

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

        public List<LanguagesBean> getLanguages() {
            return languages;
        }

        public void setLanguages(List<LanguagesBean> languages) {
            this.languages = languages;
        }


        public static class LanguagesBean {
            /**
             * id : 1
             * name : English
             */

            private int id;
            private String name;

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
        }
    }

    public static class PostsBean {
        /**
         * id : 2
         * admin_id : 1
         * post_type : 2
         * title : Lorem Ipsum Event
         * description : Lorem Ipsum Article Event
         * date_time : 2018-06-19 11:25
         * url : http://www.google.com
         * is_featured : 1
         * images : [{"id":5,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_5.png"},{"id":6,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_6.png"},{"id":7,"image_url":"h     ttps://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_7.png"}]
         */

        private int id;
        private int admin_id;
        private int post_type;
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

    }
}
