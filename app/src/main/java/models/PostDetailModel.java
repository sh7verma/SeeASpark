package models;

import java.util.List;

public class PostDetailModel extends BaseModel {

    /**
     * response : {"id":36,"post_type":2,"title":"asdasda","description":"sadds dfffd fdgfgg fggg","profession_id":"","address":"Mohali Village, Sahibzada Ajit Singh Nagar, Punjab, India","latitude":"30.7291196","longitude":"76.7171945","date_time":"2018-07-27 12:46","url":"https://www.google.com","is_featured":0,"liked":0,"like":1,"comment":0,"going":1,"going_list":[{"id":62,"full_name":"Dania ","avatar":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/avatars/cec387ef71bf055fd5ac4265141822b9.png"}],"interested":0,"is_going":0,"bookmarked":0,"images":[{"id":62,"image_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/posts/1e8b2667abdd168a1db4fb035a32dc86.png","thumbnail_url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/thumbnails/53d5928d1251348983e7ba0a6905f5f7.png"}],"shareable_link":"http://192.168.1.51:3000/posts?id=36&post_type=2"}
     * code : 111
     */

    private PostModel.ResponseBean response;
    private int code;

    public PostModel.ResponseBean getResponse() {
        return response;
    }

    public void setResponse(PostModel.ResponseBean response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
