package com.sd.lib.scatter.service.model.request.api;

import com.sd.lib.scatter.service.model.BlockChain;
import com.sd.lib.scatter.service.model.eos.EosNetwork;
import com.sd.lib.scatter.service.model.eos.EosTransaction;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestSignatureData extends ApiData
{
    private EosPayload eosPayload;

    public EosPayload getEosPayload()
    {
        return eosPayload;
    }

    private static class Payload extends BlockChain
    {
    }

    public static class EosPayload extends Payload
    {
        private EosTransaction transaction;
        private EosNetwork network;

        public EosTransaction getTransaction()
        {
            return transaction;
        }

        public EosNetwork getNetwork()
        {
            return network;
        }

        @Override
        public void read(JSONObject object) throws JSONException
        {
            super.read(object);

            final JSONObject jsonTransaction = object.optJSONObject("transaction");
            if (jsonTransaction != null)
            {
                this.transaction = new EosTransaction();
                this.transaction.read(jsonTransaction);
            }

            final JSONObject jsonNetwrok = object.optJSONObject("network");
            if (jsonNetwrok != null)
            {
                this.network = new EosNetwork();
                this.network.read(jsonNetwrok);
            }
        }
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        super.read(object);

        final JSONObject jsonPayload = object.optJSONObject("payload");
        if (jsonPayload != null)
        {
            final Payload payload = new Payload();
            payload.read(jsonPayload);

            if ("eos".equals(payload.getBlockchain()))
            {
                this.eosPayload = new EosPayload();
                this.eosPayload.read(jsonPayload);
            }
        }
    }
}
