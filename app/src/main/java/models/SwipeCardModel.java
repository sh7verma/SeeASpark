package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SwipeCardModel extends BaseModel implements Parcelable {


    /**
     * response : {"id":12,"full_name":"Gurpreet Singh","email":"gupigupigupi8@gmail.com","age":"14-06-2004","gender":"2","access_token":"fddac6a603a66b149f621b37051da1d4","account_type":1,"facebook_id":"391943651317167","profile_status":2,"linkedin_id":"","user_type":1,"email_verified":1,"skills":["Ability to Work Under Pressure"],"languages":[{"id":1,"name":"English"}],"avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_female_1.png","profession":{"id":6,"name":"Dentist","created_at":"2018-06-01T05:57:38.000Z","updated_at":"2018-06-01T05:57:38.000Z"},"bio":"Test","pro_description":"Test","experience_year":0,"experience_month":0,"document_verified":1,"preferences":{"gender":1,"distance":15,"experience_year":1,"experience_month":0,"languages":[],"skills":[],"professions":[]}}
     * is_handshake : 0
     * code : 112
     */

    private SignupModel.ResponseBean response;
    private int is_handshake;
    private int code;

    public SignupModel.ResponseBean getResponse() {
        return response;
    }

    public void setResponse(SignupModel.ResponseBean response) {
        this.response = response;
    }

    public int getIs_handshake() {
        return is_handshake;
    }

    public void setIs_handshake(int is_handshake) {
        this.is_handshake = is_handshake;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.response, flags);
        dest.writeInt(this.is_handshake);
        dest.writeInt(this.code);
    }

    public SwipeCardModel() {
    }

    protected SwipeCardModel(Parcel in) {
        this.response = in.readParcelable(SignupModel.ResponseBean.class.getClassLoader());
        this.is_handshake = in.readInt();
        this.code = in.readInt();
    }

    public static final Parcelable.Creator<SwipeCardModel> CREATOR = new Parcelable.Creator<SwipeCardModel>() {
        @Override
        public SwipeCardModel createFromParcel(Parcel source) {
            return new SwipeCardModel(source);
        }

        @Override
        public SwipeCardModel[] newArray(int size) {
            return new SwipeCardModel[size];
        }
    };
}
