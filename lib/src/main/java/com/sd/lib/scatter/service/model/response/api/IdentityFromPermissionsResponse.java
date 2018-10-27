package com.sd.lib.scatter.service.model.response.api;

import org.json.JSONException;
import org.json.JSONObject;

public class IdentityFromPermissionsResponse extends ApiResponse
{
    private String result;

    public IdentityFromPermissionsResponse(String id)
    {
        super(id);
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    @Override
    public void write(JSONObject object) throws JSONException
    {
        super.write(object);
        object.put("result", result);
    }
}
