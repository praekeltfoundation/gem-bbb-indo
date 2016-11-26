package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chris on 2016-11-22.
 */

public class ChangePassword {

    @SerializedName("old_password")
    String oldPassword;

    @SerializedName("new_password")
    String newPassword;

    public ChangePassword(String old_password,String new_password){
        this.oldPassword = old_password;
        this.newPassword = new_password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
