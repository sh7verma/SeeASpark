package models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerModel implements Parcelable {
    private int id;
    private String answers;
    private String options;
    private String question;
    private int question_type;
    private ArrayList<String> userAnswers = new ArrayList<>();

    public ArrayList<String> getuserAnswers() {
        return userAnswers;
    }

    public void setuserAnswers(ArrayList<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(int question_type) {
        this.question_type = question_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.answers);
        dest.writeString(this.options);
        dest.writeString(this.question);
        dest.writeList(this.userAnswers);
        dest.writeInt(this.question_type);
    }

    public QuestionAnswerModel() {
    }

    protected QuestionAnswerModel(Parcel in) {
        this.id = in.readInt();
        this.answers = in.readString();
        this.options = in.readString();
        this.question = in.readString();
        this.question_type = in.readInt();
        this.userAnswers = new ArrayList<>();
        in.readList(this.userAnswers, String.class.getClassLoader());
    }

    public static final Creator<QuestionAnswerModel> CREATOR = new Creator<QuestionAnswerModel>() {
        @Override
        public QuestionAnswerModel createFromParcel(Parcel source) {
            return new QuestionAnswerModel(source);
        }

        @Override
        public QuestionAnswerModel[] newArray(int size) {
            return new QuestionAnswerModel[size];
        }
    };
}
