package org.gem.indo.dooit.api.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

/**
 * Created by Wimpie Victor on 2016/11/21.
 */

public class LocalDateSerializer implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString("yyyy-MM-dd"));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(json.getAsString());
    }
}
