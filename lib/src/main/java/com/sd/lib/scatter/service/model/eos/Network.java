package com.sd.lib.scatter.service.model.eos;

import com.sd.lib.scatter.service.model.BlockChain;

import org.json.JSONException;
import org.json.JSONObject;

public class Network extends BlockChain
{
    private String protocol;
    private String host;
    private String port;
    private String chainId;

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

    public String getChainId()
    {
        return chainId;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        this.protocol = object.optString("protocol");
        this.host = object.optString("host");
        this.port = object.optString("port");
        this.chainId = object.optString("chainId");
    }
}
