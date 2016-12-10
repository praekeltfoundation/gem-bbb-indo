package org.gem.indo.dooit.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convenience class for storing values of different types.
 * <p>
 * Created by Wimpie Victor on 2016/12/06.
 */

public class ValueMap {

    private Map<String, Object> map = new LinkedHashMap<>();

    public ValueMap() {

    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**********
     * String *
     **********/

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String getString(String key) {
        return (String) map.get(key);
    }

    /***********
     * Boolean *
     ***********/

    public void put(String key, boolean value) {
        map.put(key, value);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) map.get(key);
    }

    /***********
     * Integer *
     ***********/

    public void put(String key, int value) {
        map.put(key, value);
    }

    public Integer getInteger(String key) {
        return (Integer) map.get(key);
    }

    /********
     * Long *
     ********/

    public void put(String key, long value) {
        map.put(key, value);
    }

    public Long getLong(String key) {
        return (Long) map.get(key);
    }

    /*********
     * Float *
     *********/

    public void put(String key, float value) {
        map.put(key, value);
    }

    public Float getFloat(String key) {
        return (Float) map.get(key);
    }

    /**********
     * Double *
     **********/

    public void put(String key, double value) {
        map.put(key, value);
    }

    public Double getDouble(String key) {
        return (Double) map.get(key);
    }

    /****************
     * String array *
     ****************/

    public void put(String key, String[] value) {
        map.put(key, value);
    }

    public String[] getStringArray(String key) {
        return (String[]) map.get(key);
    }

    /******************
     * Map management *
     ******************/

    public Map<String, Object> getRawMap() {
        return map;
    }
}
