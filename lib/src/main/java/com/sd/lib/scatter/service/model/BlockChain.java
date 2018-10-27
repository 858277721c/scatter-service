package com.sd.lib.scatter.service.model;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class BlockChain implements JsonReader
{
    private String blockchain;

    public String getBlockchain()
    {
        return blockchain;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.blockchain = object.optString("blockchain");
    }
}
