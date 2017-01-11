package org.gem.indo.dooit.helpers.global;

/**
 * Created by Reinhardt on 2017/01/06.
 */

public class GlobalVariables {
    private volatile boolean loginStarted = false;

    public void setLoginStarted(boolean loginStarted){
        this.loginStarted = loginStarted;
    }

    public boolean getLoginStarted(){
        return this.loginStarted;
    }
}
