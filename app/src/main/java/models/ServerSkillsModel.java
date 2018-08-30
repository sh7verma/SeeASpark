package models;

import java.util.List;

public class ServerSkillsModel extends BaseModel {


    /**
     * response : [{"id":8,"name":"Adaptability "},{"id":2,"name":"Ability to Work Under Pressure"},{"id":5,"name":"Self-motivation "},{"id":4,"name":"Time Management"},{"id":3,"name":"Decision Making"},{"id":1,"name":"Communication"},{"id":7,"name":"Leadership"},{"id":9,"name":"Teamwork"},{"id":6,"name":"Conflict Resolution"},{"id":10,"name":"Creativity"},{"id":38,"name":"Conflict Resolutionzb"},{"id":40,"name":"Leadership quality"},{"id":36,"name":"versatile"},{"id":37,"name":"Conflict Resolutiongdv"},{"id":39,"name":"hs"}]
     * code : 111
     */

    private int code;
    private List<SkillsModel> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SkillsModel> getResponse() {
        return response;
    }

    public void setResponse(List<SkillsModel> response) {
        this.response = response;
    }

}
