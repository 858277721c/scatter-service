package com.sd.lib.scatter.service.json;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonReader
{
    void read(JSONObject object) throws JSONException;
}
