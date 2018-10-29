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
import com.sd.lib.eos.rpc.utils.RpcUtils;
import com.sd.lib.scatter.service.ScatterWebSocketServer;
import com.sd.lib.scatter.service.model.eos.EosAction;
import com.sd.lib.scatter.service.model.eos.EosAuthorization;
import com.sd.lib.scatter.service.model.eos.EosNetwork;
import com.sd.lib.scatter.service.model.eos.EosTransaction;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.webview.FWebView;
import com.sd.lib.webview.client.FWebViewClient;

import java.util.List;

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
                protected GetOrRequestIdentityResponse.EosAccount getEosAccount()
                {
                    return new GetOrRequestIdentityResponse.EosAccount("ichenfq12345", "active", "EOS8PzaHQ8f7iiGR6ikqMBHXLLEJT1y4NjQ9ZsTKefP1VFYC2LCjx");
                }

                @Override
                protected List<String> signEosTransaction(EosTransaction transaction, EosNetwork network)
                {
                    final TransactionModel model = toTransactionModel(transaction);
                    final TransactionSignResult signResult = FEOSManager.getInstance().getTransactionSigner().signTransaction(model, network.getChainId(),
                            "");

                    return signResult.getSignatures();
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
            final ActionModel actionModel = new ActionModel();
            actionModel.setAccount(eosAction.getAccount());
            actionModel.setName(eosAction.getName());
            actionModel.setData(eosAction.getData());

            for (EosAuthorization eosAuthorization : eosAction.getAuthorization())
            {
                final AuthorizationModel authorizationModel = new AuthorizationModel();
                authorizationModel.setActor(eosAuthorization.getActor());
                authorizationModel.setPermission(eosAuthorization.getPermission());
                actionModel.getAuthorization().add(authorizationModel);
            }

            model.getActions().add(actionModel);
        }

        return model;
    }
}
