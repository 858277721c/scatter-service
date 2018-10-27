package com.sd.lib.scatter.service.model.request.api;

public class IdentityFromPermissionsData extends ApiData
{
    private Payload payload;

    public Payload getPayload()
    {
        return payload;
    }

    public void setPayload(Payload payload)
    {
        this.payload = payload;
    }

    public static class Payload
    {
        private String origin;

        public String getOrigin()
        {
            return origin;
        }

        public void setOrigin(String origin)
        {
            this.origin = origin;
        }
    }
}
