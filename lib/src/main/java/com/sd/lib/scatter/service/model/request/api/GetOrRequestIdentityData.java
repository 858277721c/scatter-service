package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetOrRequestIdentityData extends ApiData
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

    public static class Payload implements JsonReader
    {
        private Fields fields;

        public Fields getFields()
        {
            return fields;
        }

        public void setFields(Fields fields)
        {
            this.fields = fields;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            final Fields fields = new Fields();
            fields.read(object.getJSONObject("fields"));
            setFields(fields);
        }
    }

    public static class Fields implements JsonReader
    {
        private EosAccount eosAccount;

        public EosAccount getEosAccount()
        {
            return this.eosAccount;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            final JSONArray jsonArray = object.optJSONArray("accounts");
            if (jsonArray != null)
            {
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    final JSONObject item = jsonArray.optJSONObject(i);
                    if (item == null)
                        continue;

                    if ("eos".equals(item.optString("blockchain")))
                    {
                        final EosAccount account = new EosAccount();
                        account.read(item);
                        this.eosAccount = account;
                        break;
                    }
                }
            }
        }
    }

    private static class Account implements JsonReader
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

    public static class EosAccount extends Account
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

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);
        final Payload payload = new Payload();
        payload.read(object.getJSONObject("payload"));
        setPayload(payload);
    }
}
