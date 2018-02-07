package com.shenit.commons.utils;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * 把Date gson 为Long的Serialize
 *
 * Created by mabinbin on 17/7/18.
 */
public class DateLongSerialize implements JsonDeserializer<Date>,JsonSerializer<Date> {

    private static final Logger LOG = LoggerFactory.getLogger(GsonUtils.class);

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return new Date(json.getAsJsonPrimitive().getAsLong());
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getTime());
    }

}