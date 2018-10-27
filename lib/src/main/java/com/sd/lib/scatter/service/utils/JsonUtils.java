package com.sd.lib.scatter.service.utils;

import com.google.gson.Gson;
import com.sd.lib.scatter.service.exception.JsonException;

public class JsonUtils
{
    public static String objectToJson(Object object) throws JsonException
    {
        try
        {
            return new Gson().toJson(object);
        } catch (Exception e)
        {
            throw new JsonException(e);
        }
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) throws JsonException
    {
        try
        {
            return new Gson().fromJson(json, clazz);
        } catch (Exception e)
        {
            throw new JsonException(e);
        }
    }
}
