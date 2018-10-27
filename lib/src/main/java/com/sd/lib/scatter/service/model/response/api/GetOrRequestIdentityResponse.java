package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.exception.JsonException;
import com.sd.lib.scatter.service.utils.JsonUtils;

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

    public static class Result
    {
        private List<String> accounts;
        private Map<String, String> mapAccounts;

        private Map<String, String> getMapAccounts()
        {
            if (mapAccounts == null)
                mapAccounts = new HashMap<>(1);
            return mapAccounts;
        }

        public void setEosAccount(EosAccount account) throws JsonException
        {
            try
            {
                final String json = JsonUtils.objectToJson(account);
                final Map<String, String> mapAccounts = getMapAccounts();
                mapAccounts.put(account.getBlockchain(), json);
                this.accounts = new ArrayList<>(mapAccounts.values());
            } catch (Exception e)
            {
                throw new JsonException(e);
            }
        }
    }

    public static class EosAccount
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
    }
}
