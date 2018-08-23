package models;

import java.util.List;

public class ViewProfileModel extends BaseModel {


    /**
     * response : {"id":115,"full_name":"दिलीप चौहान","email":"defectlifecycle@gmail.com","age":"02-08-2002","gender":"1","access_token":"5056d38dd3f6d6764718d1c6b12bbb86","account_type":1,"facebook_id":"2052072875118340","profile_status":2,"linkedin_id":"","user_type":1,"email_verified":1,"skills":["Leadership","Decision Making","Adaptability "],"languages":[{"id":106,"name":"Albanian"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_6.png","profession":{"id":7,"name":"Engineer","created_at":"2018-06-01T05:57:39.000Z","updated_at":"2018-06-01T05:57:39.000Z"},"bio":"Ynunj","pro_description":"7j7j","experience_year":2,"experience_month":0,"document_verified":1,"submitted_document":0,"mentor_verified":0,"preferences":{"gender":2,"distance":0,"experience_year":3,"experience_month":0,"languages":[],"skills":[],"professions":[]},"tip":1,"mentor_question_status":0,"mentee_question_status":1,"can_switch":0,"mentor_profile_status":0,"mentee_profile_status":2,"switch_status":0}
     * code : 111
     */

    private SignupModel.ResponseBean response;
    private int code;

    public SignupModel.ResponseBean getResponse() {
        return response;
    }

    public void setResponse(SignupModel.ResponseBean response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
