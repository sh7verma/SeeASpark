package models;

import java.util.List;

public class SearchSkillModel {

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
