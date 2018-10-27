package com.sd.lib.scatter.service.model;

public class AccountModel
{
    private final String blockchain;

    public AccountModel(String blockchain)
    {
        this.blockchain = blockchain;
    }

    public String getBlockchain()
    {
        return blockchain;
    }
}
