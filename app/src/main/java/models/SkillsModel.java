package models;

import android.os.Parcel;
import android.os.Parcelable;


public class SkillsModel implements Parcelable {

    /**
     * id : 18
     * name : This
     * skill_count : 42
     * created_at : 2018-06-01T12:13:49.000Z
     * updated_at : 2018-06-04T09:20:36.000Z
     */

    private int id;
    private String name;
    private int skill_count;
    private String created_at;
    private String updated_at;
    private boolean isSelected;
    private boolean isFirstElement;

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

    public int getSkill_count() {
        return skill_count;
    }

    public void setSkill_count(int skill_count) {
        this.skill_count = skill_count;
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

    public boolean isFirstElement() {
        return isFirstElement;
    }

    public void setFirstElement(boolean firstElement) {
        isFirstElement = firstElement;
    }

    public SkillsModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.skill_count);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFirstElement ? (byte) 1 : (byte) 0);
    }

    protected SkillsModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.skill_count = in.readInt();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.isSelected = in.readByte() != 0;
        this.isFirstElement = in.readByte() != 0;
    }

    public static final Creator<SkillsModel> CREATOR = new Creator<SkillsModel>() {
        @Override
        public SkillsModel createFromParcel(Parcel source) {
            return new SkillsModel(source);
        }

        @Override
        public SkillsModel[] newArray(int size) {
            return new SkillsModel[size];
        }
    };
}
