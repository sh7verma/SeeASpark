package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class NotesListingModel extends BaseModel {


    /**
     * response : [{"id":5,"name":"3e847639862b19b883de795703a6a6c3.html","title":"Xbnxjxjxjdhxjxjjxjxcjcjjxjxjcjxjxjxjxjxjj","description":"Xbnxjxjxj<font color=\"#ee2830\">dhxjxjjxjx<\/font><font color=\"#03d68b\">cjcjjxjxjcj<\/font><font color=\"#798ded\">xjxjxjxjxjj<\/font>","url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/notes/3e847639862b19b883de795703a6a6c3.html","note_type":"1","created_at":"2018-07-24 07:06","updated_at":"2018-07-24 07:06"}]
     * code : 111
     */

    private int code;
    private List<ResponseBean> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ResponseBean> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseBean> response) {
        this.response = response;
    }

    public static class ResponseBean implements Parcelable {
        /**
         * id : 5
         * name : 3e847639862b19b883de795703a6a6c3.html
         * title : Xbnxjxjxjdhxjxjjxjxcjcjjxjxjcjxjxjxjxjxjj
         * description : Xbnxjxjxj<font color="#ee2830">dhxjxjjxjx</font><font color="#03d68b">cjcjjxjxjcj</font><font color="#798ded">xjxjxjxjxjj</font>
         * url : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/notes/3e847639862b19b883de795703a6a6c3.html
         * note_type : 1
         * created_at : 2018-07-24 07:06
         * updated_at : 2018-07-24 07:06
         */

        private int id;
        private int user_id;
        private String name;
        private String title;
        private String description;
        private String url;
        private String note_type;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNote_type() {
            return note_type;
        }

        public void setNote_type(String note_type) {
            this.note_type = note_type;
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

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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
            dest.writeInt(this.user_id);
            dest.writeString(this.name);
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeString(this.url);
            dest.writeString(this.note_type);
            dest.writeString(this.created_at);
            dest.writeString(this.updated_at);
        }

        protected ResponseBean(Parcel in) {
            this.id = in.readInt();
            this.user_id = in.readInt();
            this.name = in.readString();
            this.title = in.readString();
            this.description = in.readString();
            this.url = in.readString();
            this.note_type = in.readString();
            this.created_at = in.readString();
            this.updated_at = in.readString();
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
}
