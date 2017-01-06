package org.gem.indo.dooit.models.survey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class CoachSurvey {

    private long id;
    private String title;
    private String intro;
    private String outro;
    @SerializedName("form_fields")
    private List<CoachSurveyField> fields = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIntro() {
        return intro;
    }

    public String getOutro() {
        return outro;
    }

    public List<CoachSurveyField> getFields() {
        return fields;
    }
}
