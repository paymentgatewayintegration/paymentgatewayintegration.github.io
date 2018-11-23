package com.test.pg.sampleapp;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PGConstants {
    public static final String TAG = "PayUMoneySdk";
    public static final String GCM_SENDER_ID = "716007125784";
    public static final String TOKEN = "token";

    public static final String API_KEY = "api_key";
    public static final String AMOUNT = "amount";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String ORDER_ID = "order_id";
    public static final String CURRENCY = "currency";
    public static final String DESCRIPTION = "description";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIPCODE = "zip_code";
    public static final String COUNTRY = "country";
    public static final String RETURN_URL = "return_url";
    public static final String MODE = "mode";
    public static final String UDF1 = "udf1";
    public static final String UDF2 = "udf2";
    public static final String UDF3 = "udf3";
    public static final String UDF4 = "udf4";
    public static final String UDF5 = "udf5";
    public static final String ADDRESS_LINE1 = "address_line_1";
    public static final String ADDRESS_LINE2 = "address_line_2";

    public static final String HASH = "hash";

    public static final String RESULT = "result";
    public static final String MESSAGE = "message";
    public static final String CONTENT = "content";
    public static final String STATUS = "status";
    public static final String PAYMENT_RESPONSE = "payment_response";
    public static final String RETURN_ACTIVITY_PACKAGE = "return_activity_package";
    public static final String RETURN_ACTIVITY_CLASS = "return_activity_class";

    public static int getVersionCode(Context context) {
        if (context == null)
            return 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (Exception e) {
            return 0;
        }
    }
}
