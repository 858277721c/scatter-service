package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.json.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestSignatureResponse extends ApiResponse<RequestSignatureResponse.Result>
{
    public RequestSignatureResponse(String id)
    {
        super(id);
    }

    public static class Result implements JsonWriter
    {
        private final String transaction_id;

        public Result(String transaction_id)
        {
            this.transaction_id = transaction_id;
        }

        public String getTransaction_id()
        {
            return transaction_id;
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            object.put("transaction_id", transaction_id);
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
