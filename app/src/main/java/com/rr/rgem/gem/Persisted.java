package com.rr.rgem.gem;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by chris on 9/8/2016.
 */
public class Persisted {
    public final static String APP_PREFS = "bimbingbung_prefs_";
    final SharedPreferences settings;
    public Persisted(Context context){
        this.settings = context.getSharedPreferences(Persisted.APP_PREFS,0);
    }
    public Persisted(SharedPreferences settings){
        this.settings = settings;
    }

    public String loadConvState(String name){
        return settings.getString(name,"");
    }

    public void saveConvState(String name,String state){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name,state);
        editor.apply();
    }
    public void setRegistered(boolean registered){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("registered", registered);
        if(registered){

        }
        // Commit the edits!
        editor.apply();
    }
    public boolean isRegistered(){
        boolean loggedIn = settings.getBoolean("registered", false);
        if(!loggedIn){
            //Toast.makeText(this,"The user has not registered",300);
        }
        return  loggedIn;
    }
    public void setLoggedIn(boolean loggedIn){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("loggedIn", loggedIn);

        // Commit the edits!
        editor.apply();
    }
    public boolean isLoggedIn(){
        boolean loggedIn = settings.getBoolean("loggedIn", false);
        if(!loggedIn){
            //Toast.makeText(this,"The user has not registered",300);
        }
        return  loggedIn;
    }

}

