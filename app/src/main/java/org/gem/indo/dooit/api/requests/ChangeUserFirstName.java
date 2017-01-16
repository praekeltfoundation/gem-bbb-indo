package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2017/01/15.
 */

public class ChangeUserFirstName {

    @SerializedName("first_name")
    private String firstName;

    public ChangeUserFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }
}
