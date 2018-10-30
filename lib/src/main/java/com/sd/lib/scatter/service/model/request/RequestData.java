package com.sd.lib.scatter.service.model.request;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestData implements JsonReader
{
    private String appkey;

    public String getAppkey()
    {
        return appkey;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.appkey = object.getString("appkey");
    }
}
