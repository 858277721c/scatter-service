package com.sd.lib.scatter.service;

import android.util.Log;

import com.sd.lib.scatter.service.exception.JsonException;
import com.sd.lib.scatter.service.model.request.api.ApiData;
import com.sd.lib.scatter.service.model.request.api.GetOrRequestIdentityData;
import com.sd.lib.scatter.service.model.request.api.IdentityFromPermissionsData;
import com.sd.lib.scatter.service.model.response.api.ApiResponse;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.IdentityFromPermissionsResponse;
import com.sd.lib.scatter.service.utils.JsonUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public abstract class ScatterWebSocketServer extends WebSocketServer
{
    private static final String TAG = ScatterWebSocketServer.class.getSimpleName();

    private boolean mIsStarted;

    public ScatterWebSocketServer()
    {
        super(new InetSocketAddress(50005));
    }

    public final boolean isStarted()
    {
        return mIsStarted;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake)
    {
        Log.i(TAG, "onOpen");
        conn.send("40/scatter");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote)
    {
        Log.i(TAG, "onClose:" + code + " " + reason + " " + remote);
    }

    @Override
    public final void onMessage(WebSocket conn, String message)
    {
        Log.i(TAG, "----->:" + message);

        try
        {
            final Scatterio.Request request = Scatterio.toRequest(message);
            if (request == null)
                return;

            switch (request.dataType)
            {
                case Pair:
                    sendResponse(Scatterio.toResponse(request.dataJson, request.dataType), conn);
                    break;

                case Api:
                    final String json = new JSONObject(request.dataJson).getString("data");
                    onDataApi(json, conn);
                    break;

                default:
                    break;
            }

        } catch (JSONException e)
        {
            onDataError(new JsonException("parse message error:" + e));
            return;
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        Log.e(TAG, "onError:" + ex);
        mIsStarted = false;
    }

    @Override
    public void onStart()
    {
        Log.i(TAG, "onStart");
        mIsStarted = true;
    }

    /**
     * api数据
     *
     * @param json
     * @param socket
     */
    private void onDataApi(String json, WebSocket socket)
    {
        try
        {
            final ApiData apiData = JsonUtils.jsonToObject(json, ApiData.class);
            final String id = apiData.getId();
            if (id == null || id.isEmpty())
            {
                onDataError(new RuntimeException("api data error: id was not found in json:" + json));
                return;
            }

            final String type = apiData.getType();
            final Scatterio.ApiType apiType = Scatterio.ApiType.from(type);
            if (apiType == null)
            {
                onDataError(new RuntimeException("api data error: unknow api type:" + type));
                return;
            }

            switch (apiType)
            {
                case IdentityFromPermissions:
                    onApiTypeIdentityFromPermissions(JsonUtils.jsonToObject(json, IdentityFromPermissionsData.class), socket);
                    break;
                case GetOrRequestIdentity:
                    onApiTypeGetOrRequestIdentity(JsonUtils.jsonToObject(json, GetOrRequestIdentityData.class), socket);
                    break;
                default:
                    break;
            }
        } catch (JsonException e)
        {
            onDataError(new RuntimeException("parse api data error:" + e));
        }
    }

    private void onApiTypeIdentityFromPermissions(IdentityFromPermissionsData data, WebSocket socket)
    {
        try
        {
            final String id = data.getId();
            final IdentityFromPermissionsResponse response = new IdentityFromPermissionsResponse(id);
            response.setResult(id);

            new ApiResponser(socket).send(response);
        } catch (JsonException e)
        {
            onDataError(new RuntimeException("identityFromPermissions response error:" + e));
        }
    }

    private void onApiTypeGetOrRequestIdentity(GetOrRequestIdentityData data, WebSocket socket)
    {
        try
        {
            final GetOrRequestIdentityData.EosAccount eosAccount = data.getPayload().getFields().getEosAccount();
            if (eosAccount == null)
            {
                onDataError(new RuntimeException("eos block chain was not found in scatter request"));
                return;
            }

            final GetOrRequestIdentityResponse.EosAccount account = getEosAccount();
            if (account == null)
                throw new NullPointerException("EosAccount is null for scatter api getOrRequestIdentity");

            final GetOrRequestIdentityResponse response = new GetOrRequestIdentityResponse(data.getId());
            final GetOrRequestIdentityResponse.Result result = new GetOrRequestIdentityResponse.Result();
            result.setEosAccount(account);
            response.setResult(result);

            new ApiResponser(socket).send(response);
        } catch (JsonException e)
        {
            onDataError(new RuntimeException("getOrRequestIdentity response error:" + e));
        }
    }

    protected abstract GetOrRequestIdentityResponse.EosAccount getEosAccount();

    protected abstract void onDataError(Exception e);

    private void sendResponse(String response, WebSocket socket)
    {
        Log.i(TAG, "<-----:" + response);
        socket.send(response);
    }

    private final class ApiResponser
    {
        private final WebSocket mSocket;

        public ApiResponser(WebSocket socket)
        {
            mSocket = socket;
        }

        public void send(ApiResponse response) throws JsonException
        {
            final String json = JsonUtils.objectToJson(response);
            final String responseString = Scatterio.toResponse(json, Scatterio.DataType.Api);
            sendResponse(responseString, mSocket);
        }
    }
}