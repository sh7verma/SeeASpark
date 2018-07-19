package models;

import java.util.List;

public class ProfessionListingModel  extends BaseModel{


    /**
     * response : [{"id":3,"name":"Accountant"},{"id":1,"name":"Actor"},{"id":2,"name":"Architecture"},{"id":4,"name":"Consultant"},{"id":6,"name":"Dentist"},{"id":5,"name":"Designer"},{"id":7,"name":"Engineer"},{"id":11,"name":"ghkghkghhj"},{"id":8,"name":"Profession 1"},{"id":9,"name":"Profession 2"},{"id":10,"name":"Profession 3"},{"id":12,"name":"Tester"}]
     * code : 111
     */

    private int code;
    private List<ProfessionModel> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ProfessionModel> getResponse() {
        return response;
    }

    public void setResponse(List<ProfessionModel> response) {
        this.response = response;
    }
}
