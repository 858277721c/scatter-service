package com.sd.lib.scatter.service.model.eos;

import com.sd.lib.scatter.service.json.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Transaction implements JsonReader
{
    private String expiration;
    private long ref_block_num;
    private long ref_block_prefix;
    private long max_net_usage_words;
    private long max_cpu_usage_ms;
    private long delay_sec;
    private List<Action> actions;

    public String getExpiration()
    {
        return expiration;
    }

    public long getRef_block_num()
    {
        return ref_block_num;
    }

    public long getRef_block_prefix()
    {
        return ref_block_prefix;
    }

    public long getMax_net_usage_words()
    {
        return max_net_usage_words;
    }

    public long getMax_cpu_usage_ms()
    {
        return max_cpu_usage_ms;
    }

    public long getDelay_sec()
    {
        return delay_sec;
    }

    public List<Action> getActions()
    {
        return actions;
    }

    @Override
    public void read(JSONObject object) throws JSONException
    {
        this.expiration = object.optString("expiration");
        this.ref_block_num = object.optLong("ref_block_num");
        this.ref_block_prefix = object.optLong("ref_block_prefix");
        this.max_net_usage_words = object.optLong("max_net_usage_words");
        this.max_cpu_usage_ms = object.optLong("max_cpu_usage_ms");
        this.delay_sec = object.optLong("delay_sec");

        final JSONArray jsonArray = object.optJSONArray("actions");
        if (jsonArray != null)
        {
            final List<Action> list = new ArrayList<>(1);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                final JSONObject item = jsonArray.optJSONObject(i);
                if (item == null)
                    continue;

                final Action action = new Action();
                action.read(item);
                list.add(action);
            }
            this.actions = list;
        }
    }
}
