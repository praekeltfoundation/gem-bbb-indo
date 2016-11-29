package org.gem.indo.dooit.helpers.challenge;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Rudolph Jacobs on 2016-11-29.
 */

public class MapParameterizedType implements ParameterizedType {

    private Type keyType;
    private Type valType;

    public MapParameterizedType(Type keyType, Type valType) {
        this.keyType = keyType;
        this.valType = valType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{keyType, valType};
    }

    @Override
    public Type getRawType() {
        return HashMap.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
