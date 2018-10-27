package com.sd.lib.scatter.service.model.eos;

import com.sd.lib.scatter.service.model.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class EosNetwork extends Network
{
    private String chainId;

    public String getChainId()
    {
        return chainId;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        this.chainId = object.optString("chainId");
    }
}
