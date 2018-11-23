package com.test.pg.sampleapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    ProgressBar pb;
    private String paymentParams;
    private PGSdkInitializer.PaymentParam mPaymentParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pb = findViewById(R.id.progressBar2);
        pb.setVisibility(View.GONE);


        Button clickButton = (Button) findViewById(R.id.ee);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);

                Random rnd = new Random();
                int n = 100000 + rnd.nextInt(900000);
                SampleAppConstants.PG_ORDER_ID=Integer.toString(n);

                PGSdkInitializer.PaymentParam.Builder builder = new PGSdkInitializer.PaymentParam.Builder();
                builder.setAPiKey(SampleAppConstants.PG_API_KEY)
                        .setAmount(SampleAppConstants.PG_AMOUNT)
                        .setEmail(SampleAppConstants.PG_EMAIL)
                        .setName(SampleAppConstants.PG_NAME)
                        .setPhone(SampleAppConstants.PG_PHONE)
                        .setOrderId(SampleAppConstants.PG_ORDER_ID)
                        .setCurrency(SampleAppConstants.PG_CURRENCY)
                        .setDescription(SampleAppConstants.PG_DESCRIPTION)
                        .setCity(SampleAppConstants.PG_CITY)
                        .setState(SampleAppConstants.PG_STATE)
                        .setAddressLine1(SampleAppConstants.PG_ADD_1)
                        .setAddressLine2(SampleAppConstants.PG_ADD_2)
                        .setZipCode(SampleAppConstants.PG_ZIPCODE)
                        .setCountry(SampleAppConstants.PG_COUNTRY)
                        .setReturnUrl(SampleAppConstants.PG_RETURN_URL)
                        .setMode(SampleAppConstants.PG_MODE)
                        .setUdf1(SampleAppConstants.PG_UDF1)
                        .setUdf2(SampleAppConstants.PG_UDF2)
                        .setUdf3(SampleAppConstants.PG_UDF3)
                        .setUdf4(SampleAppConstants.PG_UDF4)
                        .setUdf5(SampleAppConstants.PG_UDF5)
                        .setReturnActivityPackage(SampleAppConstants.PG_RETURN_ACTIVITY_PACKAGE)
                        .setReturnActivityClass(SampleAppConstants.PG_RETURN_ACTIVITY_CLASS);

                mPaymentParams = builder.build();
                paymentParams = builder.buildParamsForhash(mPaymentParams);

                GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
                getHashesFromServerTask.execute(paymentParams);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        pb.setVisibility(View.GONE);
    }

    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            JSONObject response=null;
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL(SampleAppConstants.PG_HASH_URL);

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                response = new JSONObject(responseStringBuffer.toString());
                if(response.has("status")){
                    if(response.getInt("status")==0){
                        merchantHash=response.getString("hash");
                    }
                }

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    System.out.println(key+" : "+response.getString(key));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);
            pb.setVisibility(View.GONE);
            if(merchantHash.isEmpty() || merchantHash.equals("")){
                Toast.makeText(MainActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(MainActivity.this, "generated hash: "+merchantHash, Toast.LENGTH_SHORT).show();
                System.out.println("hash : "+merchantHash);

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("calculatedHash", merchantHash);
                startActivity(intent);

            }

        }

    }

}
