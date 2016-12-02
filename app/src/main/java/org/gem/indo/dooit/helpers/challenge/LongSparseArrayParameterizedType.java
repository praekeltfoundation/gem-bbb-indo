package org.gem.indo.dooit.helpers.challenge;

import android.support.v4.util.LongSparseArray;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Rudolph Jacobs on 2016-11-30.
 */

public class LongSparseArrayParameterizedType implements ParameterizedType {

    private Type type;

    public LongSparseArrayParameterizedType(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{type};
    }

    @Override
    public Type getRawType() {
        return LongSparseArray.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
