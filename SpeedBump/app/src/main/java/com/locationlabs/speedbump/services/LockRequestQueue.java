package com.locationlabs.speedbump.services;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 *
 */
public class LockRequestQueue {

    static RequestQueue rq;

    public static RequestQueue getRequestQueue(Context context) {
        if (rq == null) {
            rq = Volley.newRequestQueue(context);
        }
        return rq;
    }

}
