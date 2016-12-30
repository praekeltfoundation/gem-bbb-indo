package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by frede on 2016/12/22.
 */

public class ChangeUserEmail {
    @SerializedName("email")
    private String userEmail;

    public ChangeUserEmail(String email){
        this.userEmail = email;
    }

    public String getUsername() {
        return userEmail;
    }

    public void setUsername(String email) {
        this.userEmail = email;
    }
}
