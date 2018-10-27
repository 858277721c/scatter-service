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
            final JSONArray accounts = object.getJSONArray("accounts");
            for (int i = 0; i < accounts.length(); i++)
            {
                final JSONObject item = accounts.getJSONObject(i);
                if ("eos".equals(item.getString("blockchain")))
                {
                    final EosAccount account = new EosAccount();
                    account.read(item);
                    this.eosAccount = account;
                    break;
                }
            }
        }
    }

    public static class EosAccount implements JsonReader
    {
        private String blockchain;
        private String protocol;
        private String host;
        private String port;
        private String chainId;

        public String getBlockchain()
        {
            return blockchain;
        }

        public void setBlockchain(String blockchain)
        {
            this.blockchain = blockchain;
        }

        public String getProtocol()
        {
            return protocol;
        }

        public void setProtocol(String protocol)
        {
            this.protocol = protocol;
        }

        public String getHost()
        {
            return host;
        }

        public void setHost(String host)
        {
            this.host = host;
        }

        public String getPort()
        {
            return port;
        }

        public void setPort(String port)
        {
            this.port = port;
        }

        public String getChainId()
        {
            return chainId;
        }

        public void setChainId(String chainId)
        {
            this.chainId = chainId;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            this.blockchain = object.getString("blockchain");
            this.protocol = object.getString("protocol");
            this.host = object.getString("host");
            this.port = object.getString("port");
            this.chainId = object.getString("chainId");
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
