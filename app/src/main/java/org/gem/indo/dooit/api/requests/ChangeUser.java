package org.gem.indo.dooit.api.requests;

/**
 * Created by chris on 2016-11-21.
 */

public class ChangeUser {
    private String username;
    public ChangeUser(String name){
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }
}
