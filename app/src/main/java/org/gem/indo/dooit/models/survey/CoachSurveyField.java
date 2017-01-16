package org.gem.indo.dooit.models.survey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public class CoachSurveyField {

    /**
     * Name is the identity of the field. When submitting a form, the answers are mapped using the
     * field name.
     */
    private String name;
    private String label;
    @SerializedName("field_type")
    private FormFieldType fieldType;
    private boolean required;
    private List<String> choices = new ArrayList<>();
    @SerializedName("default_value")
    private String defaultValue;
    @SerializedName("help_text")
    private String helpText;

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public FormFieldType getFieldType() {
        return fieldType;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getHelpText() {
        return helpText;
    }
}
