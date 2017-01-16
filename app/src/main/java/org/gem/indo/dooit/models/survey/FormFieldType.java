package org.gem.indo.dooit.models.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2017/01/06.
 */

public enum FormFieldType {
    @SerializedName("singleline")
    SINGLELINE,

    @SerializedName("multiline")
    MULTILINE,

    @SerializedName("email")
    EMAIL,

    @SerializedName("number")
    NUMBER,

    @SerializedName("url")
    URL,

    @SerializedName("checkbox")
    CHECKBOX,

    @SerializedName("checkboxes")
    CHECKBOXES,

    @SerializedName("dropdown")
    DROPBOX,

    @SerializedName("radio")
    RADIO,

    @SerializedName("date")
    DATE,

    @SerializedName("datetime")
    DATETIME
}
