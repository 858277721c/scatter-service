package com.sd.lib.scatter.service.model.request.api;

public class ApiData
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
}
