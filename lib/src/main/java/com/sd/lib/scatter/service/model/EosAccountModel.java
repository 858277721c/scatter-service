package com.sd.lib.scatter.service.model;

public class EosAccountModel extends AccountModel
{
    private final String name;
    private final String authority;
    private final String publicKey;

    public EosAccountModel(String name, String authority, String publicKey)
    {
        super("eos");
        this.name = name;
        this.authority = authority;
        this.publicKey = publicKey;
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
