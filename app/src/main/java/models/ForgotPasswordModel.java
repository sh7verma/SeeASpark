package models;

public class ForgotPasswordModel extends BaseModel {

    /**
     * response : {"message":"An email has been sent to user","code":200}
     */

    private Error response;

    public Error getResponse() {
        return response;
    }

    public void setResponse(Error response) {
        this.response = response;
    }
}
