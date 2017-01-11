package org.gem.indo.dooit.helpers.GlobalVariables;

import android.content.Context;

import org.gem.indo.dooit.helpers.InvalidTokenHandler.InvalidTokenHandler;

/**
 * Created by Reinhardt on 2017/01/06.
 */

public class InvalidTokenRedirectHelper {
    private volatile boolean loginStarted = false;

    public synchronized void setLoginStarted(boolean loginStarted){
        this.loginStarted = loginStarted;
    }

    public synchronized boolean getLoginStarted(){
        return this.loginStarted;
    }

    public synchronized void redirectIfNeeded(InvalidTokenHandler invalidTokenHandler, Context context){
        if(!getLoginStarted()){
            setLoginStarted(true);
            invalidTokenHandler.handle(context);
        }
    }
}
