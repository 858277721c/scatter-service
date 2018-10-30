package com.sd.lib.scatter.service;

import com.sd.lib.scatter.service.exception.IllegalRequestEventException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Scatterio
{
    public static final String MSG_CONNECT = "40/scatter";
    public static final String MSG_EVENT = "42/scatter";

    /**
     * 解析请求消息
     *
     * @param message
     * @return
     * @throws JSONException
     */
    public static Request toRequest(String message) throws JSONException, IllegalRequestEventException
    {
        if (message == null || message.isEmpty())
            return null;

        final String msgEventPrefix = MSG_EVENT + ",";

        if (!message.startsWith(msgEventPrefix))
            return null;

        final String data = message.substring(msgEventPrefix.length());
        if (data == null || data.isEmpty())
            throw new IllegalRequestEventException("event data was not found:" + message);

        final JSONArray jsonArray = new JSONArray(data);
        if (jsonArray.length() != 2)
            throw new IllegalRequestEventException("event data format is incorrect:" + message);

        final DataType dataType = DataType.from(jsonArray.optString(0));
        if (dataType == null)
            return null;

        final String dataJson = jsonArray.optString(1);
        if (dataJson == null || dataJson.isEmpty())
            throw new IllegalRequestEventException("event data is empty:" + message);

        return new Request(dataType, dataJson);
    }

    /**
     * 转为应答消息
     *
     * @param content
     * @param dataType
     * @return
     */
    public static String toResponse(String content, DataType dataType)
    {
        final StringBuilder sb = new StringBuilder(MSG_EVENT);
        sb.append(",");
        sb.append("[");
        sb.append("\"");
        sb.append(dataType.name);
        if (DataType.Pair == dataType)
            sb.append("ed");
        sb.append("\"");

        sb.append(",");
        sb.append(content);
        sb.append("]");

        return sb.toString();
    }

    public static class Request
    {
        public final DataType dataType;
        public final Data data;

        public Request(DataType dataType, String dataJson) throws JSONException
        {
            this.dataType = dataType;

            final JSONObject jsonObject = new JSONObject(dataJson);
            final String json = jsonObject.getString("data");
            final String plugin = jsonObject.getString("plugin");

            this.data = new Data(json, plugin);
        }

        public static class Data
        {
            public final String json;
            public final String plugin;

            public Data(String json, String plugin)
            {
                this.json = json;
                this.plugin = plugin;
            }

            @Override
            public String toString()
            {
                return plugin + "," + json;
            }
        }
    }

    public enum DataType
    {
        Pair("pair"),
        Api("api");

        public final String name;

        DataType(String name)
        {
            this.name = name;
        }

        public static DataType from(String name)
        {
            for (DataType item : DataType.values())
            {
                if (item.name.equals(name))
                    return item;
            }
            return null;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public enum ApiType
    {
        IdentityFromPermissions("identityFromPermissions"),
        GetOrRequestIdentity("getOrRequestIdentity"),
        ForgetIdentity("forgetIdentity"),
        RequestSignature("requestSignature");

        public final String name;

        ApiType(String name)
        {
            this.name = name;
        }

        public static ApiType from(String name)
        {
            for (ApiType item : ApiType.values())
            {
                if (item.name.equals(name))
                    return item;
            }
            return null;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
