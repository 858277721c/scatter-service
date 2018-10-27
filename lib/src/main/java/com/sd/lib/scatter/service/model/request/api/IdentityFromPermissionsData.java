package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class IdentityFromPermissionsData extends ApiData
{
    private Payload payload;

    public Payload getPayload()
    {
        return payload;
    }

    public static class Payload implements JsonReader
    {
        private String origin;

        public String getOrigin()
        {
            return origin;
        }

        @Override
        public void read(JSONObject object)
        {
            this.origin = object.optString("origin");
        }
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);

        final JSONObject jsonPayload = object.optJSONObject("payload");
        if (jsonPayload != null)
        {
            final Payload payload = new Payload();
            payload.read(jsonPayload);
            this.payload = payload;
        }
    }
}
