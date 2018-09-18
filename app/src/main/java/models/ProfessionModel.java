package models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by applify on 6/8/2018.
 */

public class ProfessionModel implements Parcelable {

    /**
     * id : 1
     * name : Actor
     * created_at : 2018-06-01T05:57:38.000Z
     * updated_at : 2018-06-01T05:57:38.000Z
     */

    private int id;
    private String name;
    private String created_at;
    private String updated_at;
    private boolean isSelected;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ProfessionModel() {
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
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected ProfessionModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ProfessionModel> CREATOR = new Creator<ProfessionModel>() {
        @Override
        public ProfessionModel createFromParcel(Parcel source) {
            return new ProfessionModel(source);
        }

        @Override
        public ProfessionModel[] newArray(int size) {
            return new ProfessionModel[size];
        }
    };
}
