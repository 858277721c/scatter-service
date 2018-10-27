package com.sd.lib.scatter.service.model.request.api;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

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

    public static class Payload
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
    }

    public static class Fields
    {
        private List<String> accounts;

        public List<String> getAccounts()
        {
            return accounts;
        }

        public void setAccounts(List<String> accounts)
        {
            this.accounts = accounts;
        }

        public EosAccount getEosAccount()
        {
            if (accounts == null || accounts.isEmpty())
                return null;

            try
            {
                for (String item : accounts)
                {
                    final JSONObject jsonItem = new JSONObject(item);
                    final String blockchain = jsonItem.optString("blockchain");
                    if ("eos".equals(blockchain))
                    {
                        return new Gson().fromJson(item, EosAccount.class);
                    }
                }
            } catch (Exception e)
            {
            }
            return null;
        }
    }

    public static class EosAccount
    {
        private String blockchain;
        private String protocol;
        private String host;
        private int port;
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

        public int getPort()
        {
            return port;
        }

        public void setPort(int port)
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
    }
}
