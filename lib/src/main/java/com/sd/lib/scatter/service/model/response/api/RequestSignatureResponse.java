package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.json.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RequestSignatureResponse extends ApiResponse<RequestSignatureResponse.Result>
{
    public RequestSignatureResponse(String id)
    {
        super(id);
    }

    public static class Result implements JsonWriter
    {
        private final List<String> signatures;

        public Result(List<String> signatures)
        {
            this.signatures = signatures;
        }

        public List<String> getSignatures()
        {
            return signatures;
        }

        @Override
        public void write(JSONObject object) throws JSONException
        {
            final JSONArray jsonArray = new JSONArray();
            for (String item : signatures)
            {
                jsonArray.put(item);
            }
            object.put("signatures", jsonArray);
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
