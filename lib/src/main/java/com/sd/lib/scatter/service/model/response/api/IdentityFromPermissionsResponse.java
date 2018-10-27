package com.sd.lib.scatter.service.model.response.api;

import org.json.JSONException;
import org.json.JSONObject;

public class IdentityFromPermissionsResponse extends ApiResponse<String>
{
    public IdentityFromPermissionsResponse(String id)
    {
        super(id);
    }

    @Override
    public void write(JSONObject object) throws JSONException
    {
        super.write(object);
        object.put("result", getResult());
    }
}
