package com.sd.lib.scatter.service.model.response.api;

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
}
