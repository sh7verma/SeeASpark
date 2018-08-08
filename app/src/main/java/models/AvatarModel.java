package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

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
    private String selectedUrl;
    private int parentId;
    private boolean isSelected;
    private List<SkinsBean> skins;

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

    public List<SkinsBean> getSkins() {
        return skins;
    }

    public void setSkins(List<SkinsBean> skins) {
        this.skins = skins;
    }

    public String getSelectedUrl() {
        return selectedUrl;
    }

    public void setSelectedUrl(String selectedUrl) {
        this.selectedUrl = selectedUrl;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public AvatarModel() {
    }


    public static class SkinsBean implements Parcelable {
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

        public SkinsBean() {
        }

        protected SkinsBean(Parcel in) {
            this.id = in.readInt();
            this.name = in.readString();
            this.gender = in.readInt();
            this.avtar_url = in.readString();
        }

        public static final Creator<SkinsBean> CREATOR = new Creator<SkinsBean>() {
            @Override
            public SkinsBean createFromParcel(Parcel source) {
                return new SkinsBean(source);
            }

            @Override
            public SkinsBean[] newArray(int size) {
                return new SkinsBean[size];
            }
        };
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
        dest.writeString(this.selectedUrl);
        dest.writeInt(this.parentId);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.skins);
    }

    protected AvatarModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.gender = in.readInt();
        this.avtar_url = in.readString();
        this.selectedUrl = in.readString();
        this.parentId = in.readInt();
        this.isSelected = in.readByte() != 0;
        this.skins = in.createTypedArrayList(SkinsBean.CREATOR);
    }

    public static final Creator<AvatarModel> CREATOR = new Creator<AvatarModel>() {
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
