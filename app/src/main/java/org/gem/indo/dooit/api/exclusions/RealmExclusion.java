package org.gem.indo.dooit.api.exclusions;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import io.realm.RealmObject;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public class RealmExclusion implements ExclusionStrategy{

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(RealmObject.class);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
