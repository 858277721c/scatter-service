package com.sd.lib.scatter.service;

import android.util.Log;

import com.sd.lib.scatter.service.exception.JsonException;
import com.sd.lib.scatter.service.model.eos.EosNetwork;
import com.sd.lib.scatter.service.model.eos.EosTransaction;
import com.sd.lib.scatter.service.model.request.api.ApiData;
import com.sd.lib.scatter.service.model.request.api.ForgetIdentityData;
import com.sd.lib.scatter.service.model.request.api.GetOrRequestIdentityData;
import com.sd.lib.scatter.service.model.request.api.IdentityFromPermissionsData;
import com.sd.lib.scatter.service.model.request.api.RequestSignatureData;
import com.sd.lib.scatter.service.model.response.api.ApiResponse;
import com.sd.lib.scatter.service.model.response.api.ForgetIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.IdentityFromPermissionsResponse;
import com.sd.lib.scatter.service.model.response.api.RequestSignatureResponse;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.List;

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

        if (message.startsWith(Scatterio.MSG_CONNECT))
        {
            conn.send(Scatterio.MSG_CONNECT);
            return;
        }

        try
        {
            final Scatterio.Request request = Scatterio.toRequest(message);
            if (request == null)
                return;

            switch (request.dataType)
            {
                case Pair:
                    sendResponse(Scatterio.toResponse("true", Scatterio.DataType.Pair), conn);
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
            final JSONObject jsonObject = new JSONObject(json);

            final ApiData apiData = new ApiData();
            apiData.read(jsonObject);

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
                    final IdentityFromPermissionsData identityFromPermissionsData = new IdentityFromPermissionsData();
                    identityFromPermissionsData.read(jsonObject);
                    onApiTypeIdentityFromPermissions(identityFromPermissionsData, socket);
                    break;
                case GetOrRequestIdentity:
                    final GetOrRequestIdentityData getOrRequestIdentityData = new GetOrRequestIdentityData();
                    getOrRequestIdentityData.read(jsonObject);
                    onApiTypeGetOrRequestIdentity(getOrRequestIdentityData, socket);
                    break;
                case RequestSignature:
                    final RequestSignatureData requestSignatureData = new RequestSignatureData();
                    requestSignatureData.read(jsonObject);
                    onApiTypeRequestSignature(requestSignatureData, socket);
                    break;
                case ForgetIdentity:
                    final ForgetIdentityData forgetIdentityData = new ForgetIdentityData();
                    forgetIdentityData.read(jsonObject);
                    onApiTypeForgetIdentity(forgetIdentityData, socket);
                    break;
                default:
                    break;
            }
        } catch (JSONException e)
        {
            onDataError(new JsonException("parse api data error:" + e));
        }
    }

    private void onApiTypeIdentityFromPermissions(IdentityFromPermissionsData data, WebSocket socket)
    {
        final String id = data.getId();
        final IdentityFromPermissionsResponse response = new IdentityFromPermissionsResponse(id);
        response.setResult(id);

        try
        {
            new ApiResponser(socket).send(response);
        } catch (JSONException e)
        {
            onDataError(new JsonException("identityFromPermissions response error:" + e));
        }
    }

    private void onApiTypeGetOrRequestIdentity(GetOrRequestIdentityData data, WebSocket socket)
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

        try
        {
            new ApiResponser(socket).send(response);
        } catch (JSONException e)
        {
            onDataError(new JsonException("getOrRequestIdentity response error:" + e));
        }
    }

    private void onApiTypeRequestSignature(RequestSignatureData data, WebSocket socket)
    {
        final RequestSignatureData.EosPayload eosPayload = data.getEosPayload();
        final List<String> signatures = signEosTransaction(eosPayload.getTransaction(), eosPayload.getNetwork());

        final RequestSignatureResponse response = new RequestSignatureResponse(data.getId());
        response.setResult(new RequestSignatureResponse.Result(signatures));

        try
        {
            new ApiResponser(socket).send(response);
        } catch (JSONException e)
        {
            onDataError(new JsonException("requestSignature response error:" + e));
        }
    }

    private void onApiTypeForgetIdentity(ForgetIdentityData data, WebSocket socket)
    {
        final ForgetIdentityResponse response = new ForgetIdentityResponse(data.getId());
        response.setResult("");

        try
        {
            new ApiResponser(socket).send(response);
        } catch (JSONException e)
        {
            onDataError(new JsonException("forgetIdentity response error:" + e));
        }
    }

    protected abstract GetOrRequestIdentityResponse.EosAccount getEosAccount();

    protected abstract List<String> signEosTransaction(EosTransaction transaction, EosNetwork network);

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

        public void send(ApiResponse response) throws JSONException
        {
            final JSONObject jsonObject = new JSONObject();
            response.write(jsonObject);

            final String json = jsonObject.toString();
            final String responseString = Scatterio.toResponse(json, Scatterio.DataType.Api);
            sendResponse(responseString, mSocket);
        }
    }
}
