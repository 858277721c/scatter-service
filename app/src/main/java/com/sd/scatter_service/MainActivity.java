package com.sd.scatter_service;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.sd.lib.eos.rpc.core.FEOSManager;
import com.sd.lib.eos.rpc.core.output.model.ActionModel;
import com.sd.lib.eos.rpc.core.output.model.AuthorizationModel;
import com.sd.lib.eos.rpc.core.output.model.TransactionModel;
import com.sd.lib.eos.rpc.core.output.model.TransactionSignResult;
import com.sd.lib.scatter.service.ScatterWebSocketServer;
import com.sd.lib.scatter.service.model.eos.EosAction;
import com.sd.lib.scatter.service.model.eos.EosAuthorization;
import com.sd.lib.scatter.service.model.eos.EosNetwork;
import com.sd.lib.scatter.service.model.eos.EosTransaction;
import com.sd.lib.scatter.service.model.request.api.GetOrRequestIdentityData;
import com.sd.lib.scatter.service.model.request.api.RequestSignatureData;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.scatter.service.model.response.api.RequestSignatureResponse;
import com.sd.lib.webview.FWebView;
import com.sd.lib.webview.client.FWebViewClient;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    private FWebView mWebView;

    private ScatterWebSocketServer mWebSocketServer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webview);

        mWebView.setWebViewClient(new FWebViewClient(this)
        {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                handler.proceed();
            }
        });

        getWebSocketServer().start();
    }

    private ScatterWebSocketServer getWebSocketServer()
    {
        if (mWebSocketServer == null)
        {
            mWebSocketServer = new ScatterWebSocketServer()
            {
                @Override
                public void onStart()
                {
                    super.onStart();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mWebView.get("https://betdice.one/?ref=welovecasino");
                        }
                    });
                }

                @Override
                protected void onApiGetOrRequestIdentity(GetOrRequestIdentityData data, ApiResponser<GetOrRequestIdentityResponse> responser) throws JSONException
                {
                    final GetOrRequestIdentityResponse.EosAccount eosAccount = new GetOrRequestIdentityResponse.EosAccount("ichenfq12345", "active", "EOS8PzaHQ8f7iiGR6ikqMBHXLLEJT1y4NjQ9ZsTKefP1VFYC2LCjx");

                    final GetOrRequestIdentityResponse.Result result = new GetOrRequestIdentityResponse.Result();
                    result.setEosAccount(eosAccount);

                    responser.getResponse().setResult(result);
                    responser.send();
                }

                @Override
                protected void onApiRequestSignature(RequestSignatureData data, ScatterWebSocketServer.ApiResponser<RequestSignatureResponse> responser) throws JSONException
                {
                    final EosTransaction transaction = data.getEosPayload().getTransaction();
                    final EosNetwork network = data.getEosPayload().getNetwork();

                    final TransactionModel model = toTransactionModel(transaction);
                    final TransactionSignResult signResult = FEOSManager.getInstance().getTransactionSigner().signTransaction(
                            model,
                            network.getChainId(),
                            "");

                    final RequestSignatureResponse.Result result = new RequestSignatureResponse.Result(signResult.getSignatures());

                    responser.getResponse().setResult(result);
                    responser.send();
                }

                @Override
                protected void onDataError(Exception e)
                {
                    Log.e(TAG, "onDataError:" + e);
                }
            };
        }
        return mWebSocketServer;
    }

    private TransactionModel toTransactionModel(EosTransaction transaction)
    {
        final TransactionModel model = new TransactionModel();
        model.setExpiration(transaction.getExpiration());
        model.setRef_block_num(transaction.getRef_block_num());
        model.setRef_block_prefix(transaction.getRef_block_prefix());

        for (EosAction eosAction : transaction.getActions())
        {
            final ActionModel newAction = new ActionModel();
            newAction.setAccount(eosAction.getAccount());
            newAction.setName(eosAction.getName());
            newAction.setData(eosAction.getData());

            for (EosAuthorization eosAuthorization : eosAction.getAuthorization())
            {
                final AuthorizationModel newAuthorization = new AuthorizationModel();
                newAuthorization.setActor(eosAuthorization.getActor());
                newAuthorization.setPermission(eosAuthorization.getPermission());
                newAction.getAuthorization().add(newAuthorization);
            }

            model.getActions().add(newAction);
        }

        return model;
    }
}
