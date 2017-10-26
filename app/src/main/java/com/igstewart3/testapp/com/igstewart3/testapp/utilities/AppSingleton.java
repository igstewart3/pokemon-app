package com.igstewart3.testapp.com.igstewart3.testapp.utilities;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class to provide app with utilities.
 *
 * Created by ianstewart on 24/10/2017.
 */

public class AppSingleton {

    private static AppSingleton mAppSingleton = null;
    private RequestQueue mRequestQueue = null;

    /**
     * Constructor for AppSingleton instance.
     *
     * @param context The current context.
     */
    private AppSingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Retrieve the AppSingleton instance.
     *
     * @param context The current context.
     *
     * @return AppSingleton instance.
     */
    public static synchronized AppSingleton getInstance(Context context) {
        if(null == mAppSingleton) {
            mAppSingleton = new AppSingleton(context);
        }
        return mAppSingleton;
    }

    /**
     * Retrieve the RequestQueue instance for the app.
     *
     * @return RequestQueue instance
     */
    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
