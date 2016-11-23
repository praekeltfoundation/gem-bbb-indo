package org.gem.indo.dooit.api.requests;

/**
 * Created by chris on 2016-11-22.
 */

public class ChangePassword {
    String old_paswword;
    String new_passord;
    public ChangePassword(String old_password,String new_password){
        this.old_paswword = old_password;
        this.new_passord = new_password;
    }
    public String getOld_paswword() {
        return old_paswword;
    }

    public void setOld_paswword(String old_paswword) {
        this.old_paswword = old_paswword;
    }
}
