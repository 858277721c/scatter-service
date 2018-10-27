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

    public void setType(String type)
    {
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAppkey()
    {
        return appkey;
    }

    public void setAppkey(String appkey)
    {
        this.appkey = appkey;
    }

    public String getNonce()
    {
        return nonce;
    }

    public void setNonce(String nonce)
    {
        this.nonce = nonce;
    }

    public String getNextNonce()
    {
        return nextNonce;
    }

    public void setNextNonce(String nextNonce)
    {
        this.nextNonce = nextNonce;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.type = object.getString("type");
        this.id = object.getString("id");
        this.appkey = object.optString("appkey");
        this.nonce = object.optString("nonce");
        this.nextNonce = object.optString("nextNonce");
    }
}
