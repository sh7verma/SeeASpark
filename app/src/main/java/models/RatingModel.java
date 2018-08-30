package models;

/**
 * Created by dev on 29/8/18.
 */

public class RatingModel extends BaseModel {

    /**
     * response : {"id":1,"user_id":107,"other_user_id":106,"rating":4,"comment":"","created_at":"2018-08-29T06:33:06.000Z","updated_at":"2018-08-29T06:33:06.000Z"}
     * code : 111
     */

    private ResponseBean response;
    private int code;

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class ResponseBean {
        /**
         * id : 1
         * user_id : 107
         * other_user_id : 106
         * rating : 4
         * comment :
         * created_at : 2018-08-29T06:33:06.000Z
         * updated_at : 2018-08-29T06:33:06.000Z
         */

        private int id;
        private int user_id;
        private int other_user_id;
        private int rating;
        private String comment;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getOther_user_id() {
            return other_user_id;
        }

        public void setOther_user_id(int other_user_id) {
            this.other_user_id = other_user_id;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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
    }
}
