package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chris on 2016-11-22.
 */

public class ResetPassword {

    private String answer;

    @SerializedName("new_password")
    private String newPassword;

    private String username;

    public ResetPassword() {
    }

    public ResetPassword(String username, String answer, String newPassword){
        this.answer = answer;
        this.newPassword = newPassword;
        this.username = username;
    }

    public String getAnswer() {
        return answer;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getUsername() {
        return newPassword;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
