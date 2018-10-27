package com.sd.lib.scatter.service.model.response;

public abstract class ScatterResponse
{
    private final String id;

    public ScatterResponse(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}
