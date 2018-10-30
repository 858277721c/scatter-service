package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.json.JsonReader;
import com.sd.lib.scatter.service.model.request.RequestData;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiData extends RequestData
{
    private String type;
    private String id;
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
            this.origin = object.getString("origin");
        }
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        this.type = object.getString("type");
        this.id = object.getString("id");
        this.nonce = object.getString("nonce");
        this.nextNonce = object.getString("nextNonce");
    }
}
