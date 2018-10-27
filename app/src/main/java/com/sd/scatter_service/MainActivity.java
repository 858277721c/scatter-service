package com.sd.scatter_service;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.sd.lib.eos.rpc.api.RpcApi;
import com.sd.lib.eos.rpc.api.model.ApiResponse;
import com.sd.lib.eos.rpc.api.model.PushTransactionResponse;
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

import java.util.ArrayList;
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
                    return new GetOrRequestIdentityResponse.EosAccount("zjunzjun1234", "active", "EOS76weabiGCBEMemk4nnJRMTPyshEzNBqoYKCWfnMzpVqMauTYCY");
                }

                @Override
                protected String pushEosTransaction(EosTransaction transaction, EosNetwork network)
                {
                    final TransactionModel model = toTransactionModel(transaction);
                    final TransactionSignResult signResult = FEOSManager.getInstance().getTransactionSigner().signTransaction(model, network.getChainId(),
                            "");

                    try
                    {
                        final ApiResponse<PushTransactionResponse> apiResponse = new RpcApi(network.getUrl()).pushTransaction(signResult.getSignatures(),
                                signResult.getCompression(),
                                "",
                                signResult.getPacked_trx());

                        if (apiResponse.isSuccessful())
                            return apiResponse.getSuccess().getTransaction_id();

                    } catch (Exception e)
                    {
                        Log.e(TAG, String.valueOf(e));
                    }

                    return null;
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
        model.setExpiration(RpcUtils.addTime(transaction.getExpiration(), 30 * 1000));
        model.setRef_block_num(transaction.getRef_block_num());
        model.setRef_block_prefix(transaction.getRef_block_prefix());

        final List<ActionModel> listAction = new ArrayList<>();
        for (EosAction eosAction : transaction.getActions())
        {
            final ActionModel actionModel = new ActionModel();
            actionModel.setAccount(eosAction.getAccount());
            actionModel.setName(eosAction.getName());
            actionModel.setData(eosAction.getData());

            final List<AuthorizationModel> listAuthor = new ArrayList<>();
            for (EosAuthorization eosAuthorization : eosAction.getAuthorization())
            {
                final AuthorizationModel authorizationModel = new AuthorizationModel();
                authorizationModel.setActor(eosAuthorization.getActor());
                authorizationModel.setPermission(eosAuthorization.getPermission());
                listAuthor.add(authorizationModel);
            }
            actionModel.setAuthorization(listAuthor);

            listAction.add(actionModel);
        }

        model.setActions(listAction);

        return model;
    }
}
