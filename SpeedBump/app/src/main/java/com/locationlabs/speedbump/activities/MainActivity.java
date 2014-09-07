package com.locationlabs.speedbump.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.locationlabs.speedbump.R;
import com.locationlabs.speedbump.datamodel.AlertEvent;
import com.locationlabs.speedbump.services.LockDetectService;
import com.locationlabs.speedbump.utils.ColorUtil;
import com.locationlabs.speedbump.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;


public class MainActivity extends Activity {

    TextView jsonString;

    EventAdapter eventAdapter;
    ListView listview;

    int score;
    private MainReceiver mainReceiver = new MainReceiver();
    private static IntentFilter intentFilter = new IntentFilter();

    static {
        intentFilter.addAction(LockDetectService.ACTION_SCORE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);

        score = getIntent().getIntExtra(LockDetectService.EXTRA_SCORE_KEY, 0);

        if (LockDetectService.alertEvents != null && LockDetectService.alertEvents.size() != 0) {
            eventAdapter = new EventAdapter(LockDetectService.alertEvents);
            listview.setAdapter(eventAdapter);
        }

        registerReceiver(mainReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent in = new Intent(this, LockDetectService.class);
        startService(in);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mainReceiver);
    }

    public class EventAdapter extends BaseAdapter {

        ArrayList<AlertEvent> alertEvents;
        ArrayList<Integer> scores;

        EventAdapter(ArrayList<AlertEvent> alertEvents) {
            this.alertEvents = alertEvents;
            scores = new ArrayList<Integer>();

            int runningTotal = score;
            for (int i = alertEvents.size() - 1; i >= 0; i--) {
                AlertEvent event = alertEvents.get(i);
                scores.add(0, runningTotal);
                runningTotal += event.getPointValue();
            }
        }

        @Override
        public int getCount() {
            return alertEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return alertEvents.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                convertView = inflater.inflate(R.layout.event_layout, listview, false);
                holder = new ViewHolder();
                holder.eventName = (TextView) convertView.findViewById(R.id.event_id);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.eventImage = (ImageView) convertView.findViewById(R.id.event_image);
                holder.score = (TextView) convertView.findViewById(R.id.score);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AlertEvent event = alertEvents.get(position);
            holder.eventName.setText(event.getEventName());
            holder.date.setText(event.getDisplayTime());
            int id = event.getDisplayImageId();
            Drawable image;
            if (id > 0) {
                image = getResources().getDrawable(id);
            } else {
                image = null;
            }
            holder.eventImage.setImageDrawable(image);
            Integer score = scores.get(position);
            int color = ColorUtil.interpolateColorFromScore(score);
            holder.score.setText(String.valueOf(score));
            holder.score.setTextColor(color);

            return convertView;
        }
    }

    private class ViewHolder {
        TextView eventName, date, score;
        ImageView eventImage;
    }

    private class MainReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle extras = intent.getExtras();
            score = extras.getInt(LockDetectService.EXTRA_SCORE_KEY);
            if (action.equals(LockDetectService.ACTION_SCORE)) {
                Intent mainIntent = MainActivity.this.getIntent();
                mainIntent.putExtra(LockDetectService.EXTRA_SCORE_KEY, score);
                setIntent(mainIntent);

                if (LockDetectService.alertEvents != null
                    && LockDetectService.alertEvents.size() != 0
                    && (eventAdapter == null || eventAdapter.alertEvents == null)) {
                    LogUtil.e("events " + LockDetectService.alertEvents.get(0));
                    eventAdapter = new EventAdapter(LockDetectService.alertEvents);
                    listview.setAdapter(eventAdapter);
                }
            }
        }
    }
}
