package com.sd.lib.scatter.service.model.eos;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Action implements JsonReader
{
    private String account;
    private String name;
    private List<Authorization> authorization;
    private String data;

    public String getAccount()
    {
        return account;
    }

    public String getName()
    {
        return name;
    }

    public List<Authorization> getAuthorization()
    {
        return authorization;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.account = object.optString("account");
        this.name = object.optString("name");
        this.data = object.optString("data");

        final JSONArray jsonArray = object.optJSONArray("authorization");
        if (jsonArray != null)
        {
            final List<Authorization> list = new ArrayList<>(1);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                final JSONObject item = jsonArray.optJSONObject(i);
                if (item != null)
                {
                    final Authorization authorization = new Authorization();
                    authorization.read(item);
                    list.add(authorization);
                }
            }
            this.authorization = list;
        }
    }
}
