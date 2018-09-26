package com.test.pg.sampleapp;

public class SampleAppConstants {
    //API_KEY is given by the Payment Gateway. Please Copy Paste Here.
    public static final String PG_API_KEY = "<API_KEY>";

    //HOSTNAME is given by the Payment Gateway. Please Copy Paste Here.
    public static final String PG_HOSTNAME = "<HOST_NAME>";

    //URL to Accept Payment Response After Payment. This needs to be done at the client's web server.
    public static final String PG_RETURN_URL = "<RETURN_URL>";

    //Enter the Mode of Payment Here . Allowed Values are "LIVE" or "TEST".
    public static final String PG_MODE = "<MODE>";

    //PG_CURRENCY is given by the Payment Gateway. Only "INR" Allowed.
    public static final String PG_CURRENCY = "INR";

    //PG_COUNTRY is given by the Payment Gateway. Only "IND" Allowed.
    public static final String PG_COUNTRY = "IND";

}
