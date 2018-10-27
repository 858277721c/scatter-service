package com.sd.lib.scatter.service.json;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonWriter
{
    void write(JSONObject object) throws JSONException;
}
