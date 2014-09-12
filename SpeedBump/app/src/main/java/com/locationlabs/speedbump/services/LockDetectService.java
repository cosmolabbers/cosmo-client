package com.locationlabs.speedbump.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.locationlabs.speedbump.activities.LockActivity;
import com.locationlabs.speedbump.datamodel.AlertEvent;
import com.locationlabs.speedbump.enums.LockEvent;
import com.locationlabs.speedbump.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 *
 */
public class LockDetectService extends Service {
    public static final String ACTION_LOCK = "locationlabs.action.LOCK";
    public static final String ACTION_UNLOCK = "locationlabs.action.UNLOCK";
    public static final String ACTION_SCORE = "locationlabs.action.SCORE";
    public static final String EXTRA_SCORE_KEY = "extra.score.key";

    String childId = "100";
    int currentScore = -1;

    ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);

    public static ArrayList<AlertEvent> alertEvents;

    Handler handler = new Handler();

    Runnable drivingRunnable = new Runnable() {
        @Override
        public void run() {
            checkChildDriving();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("at the on create");
        handler.postDelayed(drivingRunnable, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int superVersion = super.onStartCommand(intent, flags, startId);

        LogUtil.d("at the start command");
        // possibilities.
        if (!LockActivity.isAlive) {
            handler.postDelayed(drivingRunnable, 1000);
        }

        LogUtil.e("lock activity alive? " + LockActivity.isAlive);

        return superVersion;
    }

    @Override
    public void onDestroy() {
        Intent in = new Intent(this, LockDetectService.class);
        startService(in);
        LogUtil.d("reached on destroy... Start it up again!");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void checkChildDriving() {
        RequestQueue rq = LockRequestQueue.getRequestQueue(this);
        String url ="http://evening-anchorage-9335.herokuapp.com/child/";
        url += childId;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    LogUtil.d("response is " + response.toString());
                    receivedJson(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // NO ERROR HANDLING FOR HACK DAY!
                }
            });

        rq.add(jsObjRequest);
    }

    private void receivedJson(JSONObject response) {
        boolean isDriving = false;
        JSONArray array = response.optJSONArray("alerts");
        alertEvents = new ArrayList<AlertEvent>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONArray event = array.optJSONArray(i);
                if (event != null) {
//                    LogUtil.e("event is " + event);
                    LockEvent en = LockEvent.getLockEventFromString((String) event.opt(0));

                    if (en == LockEvent.IGNITION_ON) {
                        isDriving = true;
                    } else if (en == LockEvent.IGNITION_OFF) {
                        isDriving = false;
                        if (alertEvents.get(alertEvents.size()-1).lockEvent == LockEvent.IGNITION_OFF) {
                            // we send multiple off events, so just ignore any additional at the end
                            break;
                        }
                    } else if (alertEvents.size() == 0) {
                        AlertEvent alert = new AlertEvent(en, -1);
                        alertEvents.add(alert);
                        isDriving = true;
                    }

                    long timestamp = (Long) event.opt(1);
                    AlertEvent alert = new AlertEvent(en, timestamp);
                    alertEvents.add(alert);
                }
            }
        }

        LogUtil.d("child driving is " + isDriving);

        int score = response.optInt("score", 100);
        if (currentScore < 0) currentScore = score;

        if (score < currentScore) {
            tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
            currentScore = score;
        }

        long delay = 1000;
        if (isDriving && !LockActivity.isAlive) {
            LogUtil.d("still driving, show lock screen again");
            startLockScreen(score);
        } else if (!isDriving && LockActivity.isAlive) {
            LogUtil.d("not driving, hide lock screen");
            unlockScreen(score);
        } else {
            //delay += 1500;
            sendScore(score);
        }

        //handler.removeCallbacks(drivingRunnable);
        handler.postDelayed(drivingRunnable, delay);
    }

    protected void startLockScreen(int score) {
        Intent in = new Intent(this, LockActivity.class);
        in.setAction(ACTION_LOCK);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        in.putExtra(EXTRA_SCORE_KEY, score);
        startActivity(in);
    }

    protected void unlockScreen(int score) {
        Intent in = new Intent();
        in.setAction(ACTION_UNLOCK);
        in.putExtra(EXTRA_SCORE_KEY, score);
        sendBroadcast(in);
    }

    protected void sendScore(int score) {
        Intent in = new Intent();
        in.setAction(ACTION_SCORE);
        in.putExtra(EXTRA_SCORE_KEY, score);
        sendBroadcast(in);
    }

}
