package com.sd.lib.scatter.service;

import android.util.Log;

import com.sd.lib.scatter.service.exception.IllegalRequestEventException;
import com.sd.lib.scatter.service.exception.JsonException;
import com.sd.lib.scatter.service.model.request.api.ApiData;
import com.sd.lib.scatter.service.model.request.api.ForgetIdentityData;
import com.sd.lib.scatter.service.model.request.api.GetOrRequestIdentityData;
import com.sd.lib.scatter.service.model.request.api.IdentityFromPermissionsData;
import com.sd.lib.scatter.service.model.request.api.RequestSignatureData;
import com.sd.lib.scatter.service.model.request.pair.PairData;
import com.sd.lib.scatter.service.model.response.api.ApiResponse;
import com.sd.lib.scatter.service.model.response.api.ForgetIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.IdentityFromPermissionsResponse;
import com.sd.lib.scatter.service.model.response.api.RequestSignatureResponse;
import com.sd.lib.scatter.service.utils.Sha256;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public abstract class ScatterWebSocketServer extends WebSocketServer
{
    private static final String TAG = ScatterWebSocketServer.class.getSimpleName();

    private final Map<WebSocket, WebSocketInfo> mMapSocket = new HashMap<>();
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
        mMapSocket.remove(conn);
    }

    @Override
    public final void onMessage(WebSocket conn, String message)
    {
        Log.i(TAG, "----->:" + message);

        if (message.startsWith(Scatterio.MSG_CONNECT))
        {
            sendResponse(Scatterio.MSG_CONNECT, conn);
            return;
        }

        Scatterio.Request request = null;

        try
        {
            request = Scatterio.toRequest(message);
        } catch (JSONException e)
        {
            onDataError(new JsonException("parse message error:" + e));
        } catch (IllegalRequestEventException e)
        {
            onDataError(e);
        }

        if (request != null)
        {
            switch (request.dataType)
            {
                case Pair:
                    onDataPair(request.data, conn);
                    break;

                case Api:
                    onDataApi(request.data, conn);
                    break;

                default:
                    break;
            }
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
     * pair数据
     *
     * @param data
     * @param socket
     */
    private void onDataPair(Scatterio.Request.Data data, WebSocket socket)
    {
        final String json = data.json;
        final PairData pairData = new PairData();
        try
        {
            pairData.read(new JSONObject(json));
        } catch (JSONException e)
        {
            onDataError(new JsonException("parse pair data error:" + e));
            return;
        }

        if (pairData.isPassthrough())
        {
            final WebSocketInfo info = new WebSocketInfo();
            info.setAppkey(pairData.getAppkey());
            mMapSocket.put(socket, info);
        }

        sendResponse(Scatterio.toResponse("true", Scatterio.DataType.Pair), socket);
    }

    /**
     * api数据
     *
     * @param data
     * @param socket
     */
    private void onDataApi(Scatterio.Request.Data data, WebSocket socket)
    {
        final String json = data.json;

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

            final WebSocketInfo oldInfo = mMapSocket.get(socket);
            if (oldInfo == null)
            {
                onDataError(new RuntimeException("api request was not paired before:" + data));
                return;
            }

            if (apiType != Scatterio.ApiType.IdentityFromPermissions)
            {
                if (!checkNonce(apiData, oldInfo))
                {
                    onDataError(new RuntimeException("Illegal api request check nonce failed:" + data));
                    return;
                }
            }
            oldInfo.setNextNonce(apiData.getNextNonce());


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

    private boolean checkNonce(ApiData apiData, WebSocketInfo oldInfo)
    {
        final String apiNonce = Sha256.sha(apiData.getNonce());
        final String savedNextNonce = oldInfo.getNextNonce();
        return savedNextNonce.equals(apiNonce);
    }

    private void onApiTypeIdentityFromPermissions(IdentityFromPermissionsData data, WebSocket socket)
    {
        final IdentityFromPermissionsResponse response = new IdentityFromPermissionsResponse(data.getId());
        response.setResult("true");

        try
        {
            new ApiResponser(response, socket).send();
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

        final GetOrRequestIdentityResponse response = new GetOrRequestIdentityResponse(data.getId());

        try
        {
            onApiGetOrRequestIdentity(data, new ApiResponser(response, socket));
        } catch (JSONException e)
        {
            onDataError(new JsonException("getOrRequestIdentity response error:" + e));
        }
    }

    private void onApiTypeRequestSignature(RequestSignatureData data, WebSocket socket)
    {
        final RequestSignatureResponse response = new RequestSignatureResponse(data.getId());

        try
        {
            onApiRequestSignature(data, new ApiResponser(response, socket));
        } catch (JSONException e)
        {
            onDataError(new JsonException("requestSignature response error:" + e));
        }
    }

    private void onApiTypeForgetIdentity(ForgetIdentityData data, WebSocket socket)
    {
        final ForgetIdentityResponse response = new ForgetIdentityResponse(data.getId());
        response.setResult("true");

        try
        {
            new ApiResponser(response, socket).send();
        } catch (JSONException e)
        {
            onDataError(new JsonException("forgetIdentity response error:" + e));
        }
    }

    /**
     * 登录
     *
     * @param data
     * @param responser
     * @throws JSONException
     */
    protected abstract void onApiGetOrRequestIdentity(GetOrRequestIdentityData data, ApiResponser<GetOrRequestIdentityResponse> responser) throws JSONException;

    /**
     * 签名
     *
     * @param data
     * @param responser
     * @throws JSONException
     */
    protected abstract void onApiRequestSignature(RequestSignatureData data, ApiResponser<RequestSignatureResponse> responser) throws JSONException;

    protected abstract void onDataError(Exception e);

    private void sendResponse(String response, WebSocket socket)
    {
        Log.i(TAG, "<-----:" + response);
        socket.send(response);
    }

    public final class ApiResponser<T extends ApiResponse>
    {
        private final T mResponse;
        private final WebSocket mSocket;

        public ApiResponser(T response, WebSocket socket)
        {
            if (response == null || socket == null)
                throw new NullPointerException();

            mResponse = response;
            mSocket = socket;
        }

        public T getResponse()
        {
            return mResponse;
        }

        public void send() throws JSONException
        {
            if (mResponse.getResult() == null)
                throw new RuntimeException("api response result is null");

            final JSONObject jsonObject = new JSONObject();
            mResponse.write(jsonObject);

            final String json = jsonObject.toString();
            final String responseString = Scatterio.toResponse(json, Scatterio.DataType.Api);
            sendResponse(responseString, mSocket);
        }
    }

    private static class WebSocketInfo
    {
        private String appkey;
        private String nextNonce;

        public String getAppkey()
        {
            return appkey;
        }

        public void setAppkey(String appkey)
        {
            this.appkey = appkey;
        }

        public String getNextNonce()
        {
            return nextNonce;
        }

        public void setNextNonce(String nextNonce)
        {
            this.nextNonce = nextNonce;
        }
    }
}
