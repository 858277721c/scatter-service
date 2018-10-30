package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.json.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOrRequestIdentityResponse extends ApiResponse<GetOrRequestIdentityResponse.Result>
{
    public GetOrRequestIdentityResponse(String id)
    {
        super(id);
    }

    public static class Result implements JsonWriter
    {
        private List<Account> accounts;
        private Map<String, Account> mapAccounts = new HashMap<>(1);

        public void setEosAccount(EosAccount account)
        {
            if (account == null)
                return;

            this.mapAccounts.put(account.getBlockchain(), account);
            this.accounts = new ArrayList<>(mapAccounts.values());
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            if (accounts == null || accounts.isEmpty())
                return;

            final JSONArray jsonArray = new JSONArray();
            for (Account item : accounts)
            {
                final JSONObject itemObject = new JSONObject();
                item.write(itemObject);
                jsonArray.put(itemObject);
            }

            object.put("accounts", jsonArray);
        }
    }

    private static class Account implements JsonWriter
    {
        private final String blockchain;

        public Account(String blockchain)
        {
            if (blockchain == null || blockchain.isEmpty())
                throw new NullPointerException("blockchain is null");
            this.blockchain = blockchain;
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            object.put("blockchain", blockchain);
        }

        public String getBlockchain()
        {
            return blockchain;
        }
    }

    public static class EosAccount extends Account
    {
        private final String name;
        private final String authority;
        private final String publicKey;

        public EosAccount(String name, String authority, String publicKey)
        {
            super("eos");
            this.name = name;
            this.authority = authority;
            this.publicKey = publicKey;
        }

        public String getName()
        {
            return name;
        }

        public String getAuthority()
        {
            return authority;
        }

        public String getPublicKey()
        {
            return publicKey;
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            super.write(object);
            object.put("name", name);
            object.put("authority", authority);
            object.put("publicKey", publicKey);
        }
    }

    @Override
    public void write(JSONObject object) throws JSONException
    {
        super.write(object);
        final JSONObject jsonResult = new JSONObject();
        getResult().write(jsonResult);
        object.put("result", jsonResult);
    }
}
