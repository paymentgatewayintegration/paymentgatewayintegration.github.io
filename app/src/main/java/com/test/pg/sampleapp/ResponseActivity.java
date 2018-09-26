package com.test.pg.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ResponseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.test.pg.sampleapp.R.layout.activity_response);

        ProgressBar pb;
        pb = findViewById(R.id.progressBarResponse);
        pb.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            String paymentResponse = bundle.getString("payment_response");
            try{
                JSONObject response = new JSONObject(paymentResponse);
                TextView textview = findViewById(com.test.pg.sampleapp.R.id.textView);
                TextView transid = findViewById(com.test.pg.sampleapp.R.id.transid);
                TextView code = findViewById(com.test.pg.sampleapp.R.id.rescode);
                textview.setText("Response Message : "+response.getString("response_message"));
                transid.setText("Transaction ID : "+response.getString("transaction_id"));
                code.setText("Response Code : "+response.getString("response_code"));

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    System.out.println(response.getString(key));
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed()
    {
        Intent startActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startActivity);
    }
}
