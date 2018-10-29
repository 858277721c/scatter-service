package com.sd.lib.scatter.service.model.request.api;

import org.json.JSONException;
import org.json.JSONObject;

public class IdentityFromPermissionsData extends ApiData
{
    private Payload payload;

    public Payload getPayload()
    {
        return payload;
    }

    public static class Payload extends ApiData.Payload
    {
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);

        final JSONObject jsonPayload = object.optJSONObject("payload");
        if (jsonPayload != null)
        {
            this.payload = new Payload();
            this.payload.read(jsonPayload);
        }
    }
}
