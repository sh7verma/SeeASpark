package models;

import java.util.List;

public class CommentModel extends BaseModel {


    /**
     * response : [{"id":107,"full_name":"Rajat","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":46,"description":null,"date_time":"10-07-2018 11:27"},{"id":106,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Sbbsbdbdnndndndnndndnd","date_time":"10-07-2018 11:23"},{"id":105,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Ndndnndndndnndnsndndnnd","date_time":"10-07-2018 11:23"},{"id":104,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Hsjsjsjdnndndndndnndnnd","date_time":"10-07-2018 11:23"},{"id":103,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Sjjsjsjsjksksksjks","date_time":"10-07-2018 11:23"},{"id":102,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Jzjdjdjdjdjjdhd","date_time":"10-07-2018 11:23"},{"id":101,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Hansndndndnndnx","date_time":"10-07-2018 11:23"},{"id":100,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Sjjsnsbdbndbs","date_time":"10-07-2018 11:23"},{"id":99,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Nznznzbxb","date_time":"10-07-2018 11:23"},{"id":98,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Gdhbdndndjjxjxnmd","date_time":"10-07-2018 11:23"},{"id":97,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Thank you so much Ia the ","date_time":"10-07-2018 11:20"},{"id":96,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Thank wya we will were there wwe ","date_time":"10-07-2018 11:20"},{"id":95,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Mdmdmmdmdndm","date_time":"10-07-2018 11:18"},{"id":94,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Jznnxkxkmxmx","date_time":"10-07-2018 11:18"},{"id":93,"full_name":"Hhsns","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png","user_id":232,"description":"Nsnjdnsnnd","date_time":"10-07-2018 11:18"}]
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

    public static class ResponseBean {
        /**
         * id : 107
         * full_name : Rajat
         * avatar : https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/ic_male_2.png
         * user_id : 46
         * description : null
         * date_time : 10-07-2018 11:27
         */

        private int id;
        private String full_name;
        private String avatar;
        private int user_id;
        private String description;
        private String date_time;

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

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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
    }
}
