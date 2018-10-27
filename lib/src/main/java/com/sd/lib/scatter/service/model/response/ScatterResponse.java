package com.sd.lib.scatter.service.model.response;

import com.sd.lib.scatter.service.json.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ScatterResponse implements JsonWriter
{
    private final String id;

    public ScatterResponse(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public void write(JSONObject object) throws JSONException
    {
        object.put("id", id);
    }
}
