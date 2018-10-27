package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestSignatureData extends ApiData
{
    private Payload payload;

    public Payload getPayload()
    {
        return payload;
    }

    public void setPayload(Payload payload)
    {
        this.payload = payload;
    }

    private static class Payload implements JsonReader
    {
        private String blockchain;

        public String getBlockchain()
        {
            return blockchain;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            this.blockchain = object.getString("blockchain");
        }
    }

    public static class EosPayload extends Payload
    {

    }


    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        final Payload payload = new Payload();
        payload.read(object.getJSONObject("payload"));
        setPayload(payload);
    }
}
