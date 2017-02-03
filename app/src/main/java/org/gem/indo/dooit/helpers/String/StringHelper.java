package org.gem.indo.dooit.helpers.String;

/**
 * Created by Reinhardt on 2017/02/01.
 */

public class StringHelper {
    public static String newString(String string){
        if(string == null){
            return null;
        }else{
            return new String(string);
        }
    }
}
