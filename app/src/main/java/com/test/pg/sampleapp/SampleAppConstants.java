package com.test.pg.sampleapp;

public class SampleAppConstants {
    //API_KEY is given by the Payment Gateway. Please Copy Paste Here.
    public static final String PG_API_KEY = "<API_KEY>";

    //HOSTNAME is given by the Payment Gateway. Please Copy Paste Here.
    public static final String PG_HOSTNAME = "<HOST_NAME>";

    //URL to Accept Payment Response After Payment. This needs to be done at the client's web server.
    public static final String PG_RETURN_URL = "<RETURN_URL>";

    //URL to calculate the Hash for the payment request parameters. This needs to be done at the client's web server.
    public static final String PG_HASH_URL = "<your_hash_url>";

    //Enter the Mode of Payment Here . Allowed Values are "LIVE" or "TEST".
    public static final String PG_MODE = "<MODE>";

    //PG_CURRENCY is given by the Payment Gateway. Only "INR" Allowed.
    public static final String PG_CURRENCY = "INR";

    //PG_COUNTRY is given by the Payment Gateway. Only "IND" Allowed.
    public static final String PG_COUNTRY = "IND";


    public static final String PG_AMOUNT = "2.00";
    public static final String PG_EMAIL = "abc@test.com";
    public static final String PG_NAME = "test";
    public static final String PG_PHONE = "9876543210";
    public static String PG_ORDER_ID = "";
    public static final String PG_DESCRIPTION = "test";
    public static final String PG_CITY = "Bangalore";
    public static final String PG_STATE = "Karnataka";
    public static final String PG_ADD_1 = "abc";
    public static final String PG_ADD_2 = "abc";
    public static final String PG_ZIPCODE = "123456";
    public static final String PG_UDF1 = "udf1";
    public static final String PG_UDF2 = "udf2";
    public static final String PG_UDF3 = "udf3";
    public static final String PG_UDF4 = "udf4";
    public static final String PG_UDF5 = "udf5";
    public static final String PG_RETURN_ACTIVITY_PACKAGE = "com.test.pg.sampleapp";
    public static final String PG_RETURN_ACTIVITY_CLASS = "com.test.pg.sampleapp.ResponseActivity";

}
