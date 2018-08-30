package models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by applify on 6/8/2018.
 */

public class LanguageModel implements Parcelable {

    /**
     * id : 1
     * name : English
     * created_at : 2018-06-01T12:34:23.000Z
     * updated_at : 2018-06-01T12:34:23.000Z
     * creat 06-08 11:29:36.804 18642-24448/com.seeaspark D/OkHttp: ed_at : 2018-06-01T12:34:24.000Z
     */

    private int id;
    private String name;
    private String created_at;
    private String updated_at;
    private boolean isSelected;
    @SerializedName("creat 06-08 11:29:36.804 18642-24448/com.seeaspark D/OkHttp: ed_at")
    private String _$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194; // FIXME check this code

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

    public String get_$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194() {
        return _$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194;
    }

    public void set_$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194(String _$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194) {
        this._$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194 = _$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public LanguageModel() {
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
        dest.writeString(this._$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194);
    }

    protected LanguageModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.isSelected = in.readByte() != 0;
        this._$Creat06081129368041864224448ComSeeasparkDOkHttpEd_at194 = in.readString();
    }

    public static final Creator<LanguageModel> CREATOR = new Creator<LanguageModel>() {
        @Override
        public LanguageModel createFromParcel(Parcel source) {
            return new LanguageModel(source);
        }

        @Override
        public LanguageModel[] newArray(int size) {
            return new LanguageModel[size];
        }
    };
}
