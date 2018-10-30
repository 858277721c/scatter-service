package com.sd.lib.scatter.service;

import org.json.JSONArray;
import org.json.JSONException;

public class Scatterio
{
    public static final String MSG_PREFIX = "42/scatter,";
    public static final String MSG_CONNECT = "40/scatter";

    /**
     * 解析请求消息
     *
     * @param message
     * @return
     * @throws JSONException
     */
    public static Request toRequest(String message) throws JSONException
    {
        if (message == null || message.isEmpty())
            return null;

        if (!message.startsWith(MSG_PREFIX))
            return null;

        final String data = message.substring(MSG_PREFIX.length());
        if (data == null || data.isEmpty())
            return null;

        final JSONArray jsonArray = new JSONArray(data);
        if (jsonArray.length() != 2)
            return null;

        final DataType dataType = DataType.from(jsonArray.optString(0));
        if (dataType == null)
            return null;

        final String dataJson = jsonArray.optString(1);
        return new Request(dataType, dataJson);
    }

    /**
     * 转为应答消息
     *
     * @param json
     * @param dataType
     * @return
     */
    public static String toResponse(String json, DataType dataType)
    {
        final StringBuilder sb = new StringBuilder(MSG_PREFIX);
        sb.append("[");
        sb.append("\"");
        sb.append(dataType.name);
        if (DataType.Pair == dataType)
            sb.append("ed");
        sb.append("\"");

        sb.append(",");
        sb.append(json);
        sb.append("]");

        return sb.toString();
    }

    public static class Request
    {
        public final DataType dataType;
        public final String dataJson;

        public Request(DataType dataType, String dataJson)
        {
            this.dataType = dataType;
            this.dataJson = dataJson;
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
