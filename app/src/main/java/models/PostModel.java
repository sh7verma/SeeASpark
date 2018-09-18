package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PostModel extends BaseModel {


    /**
     * response : [{"id":2,"post_type":2,"title":"Lorem Ipsum Event","description":"Lorem Ipsum Article Event","profession_id":"Actor","address":"","latitude":"","longitude":"","date_time":"19-06-2018 11:25","url":"http://www.google.com","is_featured":1,"like":0,"comment":0,"going":0,"going_list":[],"interested":0,"images":[{"id":5,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/posts/2.png","thumbnail_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/thumbnails/2.png"}]}]
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
         * id : 2
         * post_type : 2
         * title : Lorem Ipsum Event
         * description : Lorem Ipsum Article Event
         * profession_id : Actor
         * address :
         * latitude :
         * longitude :
         * date_time : 19-06-2018 11:25
         * url : http://www.google.com
         * is_featured : 1
         * like : 0
         * comment : 0
         * going : 0
         * going_list : []
         * interested : 0
         * images : [{"id":5,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/posts/2.png","thumbnail_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/thumbnails/2.png"}]
         */

        private int id;
        private int post_type;
        private String title;
        private String description;
        private String profession_id;
        private String address;
        private String latitude;
        private String longitude;
        private String date_time;
        private String url;
        private String shareable_link;
        private int is_featured;
        private int like;
        private int comment;
        private int going;
        private int interested;
        private int liked;
        private int is_going;
        private int bookmarked;
        private List<GoingUserBean> going_list;
        private List<ImagesBean> images;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getProfession_id() {
            return profession_id;
        }

        public void setProfession_id(String profession_id) {
            this.profession_id = profession_id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
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

        public String getShareable_link() {
            return shareable_link;
        }

        public void setShareable_link(String shareable_link) {
            this.shareable_link = shareable_link;
        }

        public int getIs_featured() {
            return is_featured;
        }

        public void setIs_featured(int is_featured) {
            this.is_featured = is_featured;
        }

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getIs_going() {
            return is_going;
        }

        public void setIs_going(int is_going) {
            this.is_going = is_going;
        }

        public int getGoing() {
            return going;
        }

        public void setGoing(int going) {
            this.going = going;
        }

        public int getInterested() {
            return interested;
        }

        public int getLiked() {
            return liked;
        }

        public void setLiked(int liked) {
            this.liked = liked;
        }

        public int getBookmarked() {
            return bookmarked;
        }

        public void setBookmarked(int bookmarked) {
            this.bookmarked = bookmarked;
        }

        public void setInterested(int interested) {
            this.interested = interested;
        }

        public List<GoingUserBean> getGoing_list() {
            return going_list;
        }

        public void setGoing_list(List<GoingUserBean> going_list) {
            this.going_list = going_list;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public static class ImagesBean implements Parcelable {
            /**
             * id : 5
             * image_url : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/posts/2.png
             * thumbnail_url : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/thumbnails/2.png
             */

            private int id;
            private String image_url;
            private String thumbnail_url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getThumbnail_url() {
                return thumbnail_url;
            }

            public void setThumbnail_url(String thumbnail_url) {
                this.thumbnail_url = thumbnail_url;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.image_url);
                dest.writeString(this.thumbnail_url);
            }

            public ImagesBean() {
            }

            protected ImagesBean(Parcel in) {
                this.id = in.readInt();
                this.image_url = in.readString();
                this.thumbnail_url = in.readString();
            }

            public static final Creator<ImagesBean> CREATOR = new Creator<ImagesBean>() {
                @Override
                public ImagesBean createFromParcel(Parcel source) {
                    return new ImagesBean(source);
                }

                @Override
                public ImagesBean[] newArray(int size) {
                    return new ImagesBean[size];
                }
            };
        }

        public static class GoingUserBean implements Parcelable {

            private int id;
            private String full_name;
            private String avatar;

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

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeString(this.full_name);
                dest.writeString(this.avatar);
            }

            public GoingUserBean() {
            }

            protected GoingUserBean(Parcel in) {
                this.id = in.readInt();
                this.full_name = in.readString();
                this.avatar = in.readString();
            }

            public static final Creator<GoingUserBean> CREATOR = new Creator<GoingUserBean>() {
                @Override
                public GoingUserBean createFromParcel(Parcel source) {
                    return new GoingUserBean(source);
                }

                @Override
                public GoingUserBean[] newArray(int size) {
                    return new GoingUserBean[size];
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
            dest.writeInt(this.post_type);
            dest.writeString(this.title);
            dest.writeString(this.description);
            dest.writeString(this.profession_id);
            dest.writeString(this.address);
            dest.writeString(this.latitude);
            dest.writeString(this.longitude);
            dest.writeString(this.date_time);
            dest.writeString(this.url);
            dest.writeString(this.shareable_link);
            dest.writeInt(this.is_featured);
            dest.writeInt(this.like);
            dest.writeInt(this.comment);
            dest.writeInt(this.going);
            dest.writeInt(this.interested);
            dest.writeInt(this.liked);
            dest.writeInt(this.is_going);
            dest.writeInt(this.bookmarked);
            dest.writeTypedList(this.going_list);
            dest.writeTypedList(this.images);
        }

        protected ResponseBean(Parcel in) {
            this.id = in.readInt();
            this.post_type = in.readInt();
            this.title = in.readString();
            this.description = in.readString();
            this.profession_id = in.readString();
            this.address = in.readString();
            this.latitude = in.readString();
            this.longitude = in.readString();
            this.date_time = in.readString();
            this.url = in.readString();
            this.shareable_link = in.readString();
            this.is_featured = in.readInt();
            this.like = in.readInt();
            this.comment = in.readInt();
            this.going = in.readInt();
            this.interested = in.readInt();
            this.liked = in.readInt();
            this.is_going = in.readInt();
            this.bookmarked = in.readInt();
            this.going_list = in.createTypedArrayList(GoingUserBean.CREATOR);
            this.images = in.createTypedArrayList(ImagesBean.CREATOR);
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
