package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuestionListingModel extends BaseModel implements Parcelable {


    /**
     * response : [{"id":1,"question":"What Experience you're expecting from your Mentor?","options":"0-5,6-10,11-15,16-20","answers":""},{"id":3,"question":"What Skills do you need from your mentor?","options":"Communication,Ability to Work Under Pressure,Decision Making,Time Management","answers":""},{"id":7,"question":"What Professions does the Mentor should belong?","options":"Actor,Architecture,Accountant,Consultant","answers":""},{"id":8,"question":"What kind of mentor are you looking for?","options":"Educational,Career,Life Coach,Undecided","answers":""}]
     * code : 111
     */

    private int code;
    private List<QuestionAnswerModel> response;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<QuestionAnswerModel> getResponse() {
        return response;
    }

    public void setResponse(List<QuestionAnswerModel> response) {
        this.response = response;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeTypedList(this.response);
    }

    public QuestionListingModel() {
    }

    protected QuestionListingModel(Parcel in) {
        this.code = in.readInt();
        this.response = in.createTypedArrayList(QuestionAnswerModel.CREATOR);
    }

    public static final Creator<QuestionListingModel> CREATOR = new Creator<QuestionListingModel>() {
        @Override
        public QuestionListingModel createFromParcel(Parcel source) {
            return new QuestionListingModel(source);
        }

        @Override
        public QuestionListingModel[] newArray(int size) {
            return new QuestionListingModel[size];
        }
    };
}
