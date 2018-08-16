package models;

public class NotesModel extends BaseModel {


    /**
     * response : {"id":10,"name":"4b473b3d3587cff1792bbd0bfff92226.html","title":"Xhhxbdbddhdhhdhdhdhdhdhdhhdhdhdh","description":"Xhhxbdbd<font color=\"#ee2830\">dhdhhdhdhdhdh<\/font>dhdhhdhdhdh","url":"https://s3.ap-south-1.amazonaws.com/kittydev/see_a_spark/notes/4b473b3d3587cff1792bbd0bfff92226.html","note_type":1,"full_name":"Sonal Malik","created_at":"2018-07-24 09:00","updated_at":"2018-07-24 09:00"}
     * code : 111
     */

    private NotesListingModel.ResponseBean response;
    private int code;

    public NotesListingModel.ResponseBean getResponse() {
        return response;
    }

    public void setResponse(NotesListingModel.ResponseBean response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
