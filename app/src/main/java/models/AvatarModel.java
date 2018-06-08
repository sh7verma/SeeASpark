package models;

import android.os.Parcel;
import android.os.Parcelable;

public class AvatarModel implements Parcelable {

    /**
     * id : 1
     * name : ic_avatar_1.png
     * gender : 1
     * avtar_url : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_avatar_1.png
     */

    private int id;
    private String name;
    private int gender;
    private String avtar_url;

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvtar_url() {
        return avtar_url;
    }

    public void setAvtar_url(String avtar_url) {
        this.avtar_url = avtar_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.gender);
        dest.writeString(this.avtar_url);
    }

    public AvatarModel() {
    }

    protected AvatarModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.gender = in.readInt();
        this.avtar_url = in.readString();
    }

    public static final Parcelable.Creator<AvatarModel> CREATOR = new Parcelable.Creator<AvatarModel>() {
        @Override
        public AvatarModel createFromParcel(Parcel source) {
            return new AvatarModel(source);
        }

        @Override
        public AvatarModel[] newArray(int size) {
            return new AvatarModel[size];
        }
    };
}
