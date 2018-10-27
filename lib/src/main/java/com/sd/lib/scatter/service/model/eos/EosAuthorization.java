package com.sd.lib.scatter.service.model.eos;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class EosAuthorization implements JsonReader
{
    private String actor;
    private String permission;

    public String getActor()
    {
        return actor;
    }

    public String getPermission()
    {
        return permission;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.actor = object.optString("actor");
        this.permission = object.optString("permission");
    }
}
