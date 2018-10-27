package com.sd.lib.scatter.service.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Network extends BlockChain
{
    private String protocol;
    private String host;
    private String port;

    public String getUrl()
    {
        return protocol + "://" + host + ":" + port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public String getHost()
    {
        return host;
    }

    public String getPort()
    {
        return port;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        this.protocol = object.optString("protocol");
        this.host = object.optString("host");
        this.port = object.optString("port");
    }
}
