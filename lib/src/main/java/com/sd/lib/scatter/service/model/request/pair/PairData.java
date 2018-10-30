package com.sd.lib.scatter.service.model.request.pair;

import com.sd.lib.scatter.service.model.request.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class PairData extends RequestData
{
    private String origin;
    private boolean passthrough;

    public String getOrigin()
    {
        return origin;
    }

    public boolean isPassthrough()
    {
        return passthrough;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        this.origin = object.getString("origin");
        this.passthrough = object.getBoolean("passthrough");
    }
}
