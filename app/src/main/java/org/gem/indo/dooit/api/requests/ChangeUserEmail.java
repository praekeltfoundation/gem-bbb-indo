package org.gem.indo.dooit.api.requests;

/**
 * Created by frede on 2016/12/22.
 */

public class ChangeUserEmail {

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
