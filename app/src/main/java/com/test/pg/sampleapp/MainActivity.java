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

                //Make query string parameters from the user input.
                StringBuffer postParamsBuffer = new StringBuffer();
                postParamsBuffer.append(concatParams(PGConstants.API_KEY, SampleAppConstants.PG_API_KEY));
                postParamsBuffer.append(concatParams(PGConstants.AMOUNT, SampleAppConstants.PG_AMOUNT));
                postParamsBuffer.append(concatParams(PGConstants.EMAIL, SampleAppConstants.PG_EMAIL));
                postParamsBuffer.append(concatParams(PGConstants.NAME, SampleAppConstants.PG_NAME));
                postParamsBuffer.append(concatParams(PGConstants.PHONE, SampleAppConstants.PG_PHONE));
                postParamsBuffer.append(concatParams(PGConstants.ORDER_ID, SampleAppConstants.PG_ORDER_ID));
                postParamsBuffer.append(concatParams(PGConstants.CURRENCY, SampleAppConstants.PG_CURRENCY));
                postParamsBuffer.append(concatParams(PGConstants.DESCRIPTION, SampleAppConstants.PG_DESCRIPTION));
                postParamsBuffer.append(concatParams(PGConstants.CITY, SampleAppConstants.PG_CITY));
                postParamsBuffer.append(concatParams(PGConstants.STATE, SampleAppConstants.PG_STATE));
                postParamsBuffer.append(concatParams(PGConstants.ADDRESS_LINE1, SampleAppConstants.PG_ADD_1));
                postParamsBuffer.append(concatParams(PGConstants.ADDRESS_LINE2, SampleAppConstants.PG_ADD_2));
                postParamsBuffer.append(concatParams(PGConstants.ZIPCODE, SampleAppConstants.PG_ZIPCODE));
                postParamsBuffer.append(concatParams(PGConstants.COUNTRY, SampleAppConstants.PG_COUNTRY));
                postParamsBuffer.append(concatParams(PGConstants.RETURN_URL, SampleAppConstants.PG_RETURN_URL));
                postParamsBuffer.append(concatParams(PGConstants.MODE, SampleAppConstants.PG_MODE));
                postParamsBuffer.append(concatParams(PGConstants.UDF1, SampleAppConstants.PG_UDF1));
                postParamsBuffer.append(concatParams(PGConstants.UDF2, SampleAppConstants.PG_UDF2));
                postParamsBuffer.append(concatParams(PGConstants.UDF3, SampleAppConstants.PG_UDF3));
                postParamsBuffer.append(concatParams(PGConstants.UDF4, SampleAppConstants.PG_UDF4));
                postParamsBuffer.append(concatParams(PGConstants.UDF5, SampleAppConstants.PG_UDF5));

                String hashParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

                //To obtain the Hash Key for the payment details, POST the query string obtained above
                // to your WebServer's Hash API using the below AsyncTask.
                GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
                getHashesFromServerTask.execute(hashParams);
            }
        });
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    @Override
    public void onStop() {
        super.onStop();
        pb.setVisibility(View.GONE);
    }


    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {

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
                System.out.println("Calculated Hash : "+merchantHash);

                //Now send the Hash to the Payment Activity.
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra("calculatedHash", merchantHash);
                startActivity(intent);

            }

        }

    }
}
