package models;

public class NotificationModel extends BaseModel {


    /**
     * response : {"messages_notification":0,"posts_notification":0,"qoutes_notification":0,"notes_notification":0}
     * code : 604
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
         * messages_notification : 0
         * posts_notification : 0
         * qoutes_notification : 0
         * notes_notification : 0
         */

        private int messages_notification;
        private int posts_notification;
        private int qoutes_notification;
        private int notes_notification;

        public int getMessages_notification() {
            return messages_notification;
        }

        public void setMessages_notification(int messages_notification) {
            this.messages_notification = messages_notification;
        }

        public int getPosts_notification() {
            return posts_notification;
        }

        public void setPosts_notification(int posts_notification) {
            this.posts_notification = posts_notification;
        }

        public int getQoutes_notification() {
            return qoutes_notification;
        }

        public void setQoutes_notification(int qoutes_notification) {
            this.qoutes_notification = qoutes_notification;
        }

        public int getNotes_notification() {
            return notes_notification;
        }

        public void setNotes_notification(int notes_notification) {
            this.notes_notification = notes_notification;
        }
    }
}
