package com.sd.lib.scatter.service.model.response.api;

import com.sd.lib.scatter.service.model.response.ScatterResponse;

public class ApiResponse<R> extends ScatterResponse
{
    private R result;

    public ApiResponse(String id)
    {
        super(id);
    }

    public R getResult()
    {
        return result;
    }

    public void setResult(R result)
    {
        this.result = result;
    }
}
