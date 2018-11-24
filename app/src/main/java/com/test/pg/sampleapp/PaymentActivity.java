package com.test.pg.sampleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;

public class PaymentActivity extends AppCompatActivity {
    ProgressBar pb;
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        webview = findViewById(R.id.webview);
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);

        String calculatedHash= getIntent().getStringExtra("calculatedHash");
        //Before initiating payment, you should calculate the hash for the payment request parameters.
        //Please refer to the integration documentation for more details.


        //Now again create a query string of the payment parameters with hash obtained from the previous activity.
        try{
            StringBuffer requestParams=new StringBuffer("api_key="+URLDecoder.decode(SampleAppConstants.PG_API_KEY, "UTF-8"));
            requestParams.append("&amount="+URLDecoder.decode(SampleAppConstants.PG_AMOUNT, "UTF-8"));
            requestParams.append("&email="+URLDecoder.decode(SampleAppConstants.PG_EMAIL, "UTF-8"));
            requestParams.append("&name="+URLDecoder.decode(SampleAppConstants.PG_NAME, "UTF-8"));
            requestParams.append("&phone="+URLDecoder.decode(SampleAppConstants.PG_PHONE, "UTF-8"));
            requestParams.append("&order_id="+URLDecoder.decode(SampleAppConstants.PG_ORDER_ID, "UTF-8"));
            requestParams.append("&currency="+URLDecoder.decode(SampleAppConstants.PG_CURRENCY, "UTF-8"));
            requestParams.append("&description="+URLDecoder.decode(SampleAppConstants.PG_DESCRIPTION, "UTF-8"));
            requestParams.append("&city="+URLDecoder.decode(SampleAppConstants.PG_CITY, "UTF-8"));
            requestParams.append("&state="+URLDecoder.decode(SampleAppConstants.PG_STATE, "UTF-8"));
            requestParams.append("&address_line_1="+URLDecoder.decode(SampleAppConstants.PG_ADD_1, "UTF-8"));
            requestParams.append("&address_line_2="+URLDecoder.decode(SampleAppConstants.PG_ADD_2, "UTF-8"));
            requestParams.append("&zip_code="+URLDecoder.decode(SampleAppConstants.PG_ZIPCODE, "UTF-8"));
            requestParams.append("&country="+URLDecoder.decode(SampleAppConstants.PG_COUNTRY, "UTF-8"));
            requestParams.append("&return_url="+URLDecoder.decode(SampleAppConstants.PG_RETURN_URL, "UTF-8"));
            requestParams.append("&mode="+URLDecoder.decode(SampleAppConstants.PG_MODE, "UTF-8"));
            requestParams.append("&udf1="+URLDecoder.decode(SampleAppConstants.PG_UDF1, "UTF-8"));
            requestParams.append("&udf2="+URLDecoder.decode(SampleAppConstants.PG_UDF2, "UTF-8"));
            requestParams.append("&udf3="+URLDecoder.decode(SampleAppConstants.PG_UDF3, "UTF-8"));
            requestParams.append("&udf4="+URLDecoder.decode(SampleAppConstants.PG_UDF4, "UTF-8"));
            requestParams.append("&udf5="+URLDecoder.decode(SampleAppConstants.PG_UDF5, "UTF-8"));
            requestParams.append("&hash="+URLDecoder.decode(calculatedHash, "UTF-8"));


            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    pb.setVisibility(View.GONE);

                    if(url.equals(SampleAppConstants.PG_RETURN_URL)){
                        view.setVisibility(View.GONE);
                        view.loadUrl("javascript:HtmlViewer.showHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    pb.setVisibility(View.VISIBLE);
                }

            });

            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setDomStorageEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
            //Not POST the query string obtained above to Payment Gateway's Payment API in a webview.
            webview.postUrl(SampleAppConstants.PG_HOSTNAME+"/v2/paymentrequest",requestParams.toString().getBytes());

        }catch (Exception e){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Toast.makeText(getBaseContext(), exceptionAsString,Toast.LENGTH_SHORT).show();
        }


    }

    class MyJavaScriptInterface {
        @JavascriptInterface
        public void showHTML(String html) {
            //On completion of the payment, the response from your WebServer's API will be obtained here.
            Intent intent=new Intent(getBaseContext(), ResponseActivity.class);
            intent.putExtra("payment_response", Html.fromHtml(html).toString());
            startActivity(intent);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
