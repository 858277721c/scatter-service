package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.json.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOrRequestIdentityResponse extends ApiResponse
{
    private Result result;

    public GetOrRequestIdentityResponse(String id)
    {
        super(id);
    }

    public Result getResult()
    {
        return result;
    }

    public void setResult(Result result)
    {
        this.result = result;
    }

    public static class Result implements JsonWriter
    {
        private List<String> accounts;
        private Map<String, String> mapAccounts = new HashMap<>(1);

        public void setEosAccount(EosAccount account) throws JSONException
        {
            final JSONObject jsonObject = new JSONObject();
            account.write(jsonObject);

            this.mapAccounts.put(account.getBlockchain(), jsonObject.toString());
            this.accounts = new ArrayList<>(mapAccounts.values());
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            if (accounts == null || accounts.isEmpty())
                return;

            final JSONArray jsonArray = new JSONArray();
            for (String item : accounts)
            {
                jsonArray.put(item);
            }

            object.put("accounts", accounts);
        }
    }

    public static class EosAccount implements JsonWriter
    {
        private final String blockchain = "eos";
        private final String name;
        private final String authority;
        private final String publicKey;

        public EosAccount(String name, String authority, String publicKey)
        {
            this.name = name;
            this.authority = authority;
            this.publicKey = publicKey;
        }

        public String getBlockchain()
        {
            return blockchain;
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
            object.put("blockchain", blockchain);
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
