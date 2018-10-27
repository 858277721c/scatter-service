package com.sd.scatter_service;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.sd.lib.scatter.service.ScatterWebSocketServer;
import com.sd.lib.scatter.service.model.eos.EosNetwork;
import com.sd.lib.scatter.service.model.eos.EosTransaction;
import com.sd.lib.scatter.service.model.response.api.GetOrRequestIdentityResponse;
import com.sd.lib.webview.FWebView;
import com.sd.lib.webview.client.FWebViewClient;

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
                protected void pushEosTransaction(EosTransaction transaction, EosNetwork network)
                {

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
}
