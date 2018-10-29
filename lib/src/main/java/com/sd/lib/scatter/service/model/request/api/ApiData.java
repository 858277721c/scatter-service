package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiData implements JsonReader
{
    private String type;
    private String id;
    private String appkey;
    private String nonce;
    private String nextNonce;

    public String getType()
    {
        return type;
    }

    public String getId()
    {
        return id;
    }

    public String getAppkey()
    {
        return appkey;
    }

    public String getNonce()
    {
        return nonce;
    }

    public String getNextNonce()
    {
        return nextNonce;
    }

    public static class Payload implements JsonReader
    {
        private String origin;

        public String getOrigin()
        {
            return origin;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            this.origin = object.optString("origin");
        }
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.type = object.optString("type");
        this.id = object.optString("id");
        this.appkey = object.optString("appkey");
        this.nonce = object.optString("nonce");
        this.nextNonce = object.optString("nextNonce");
    }
}
