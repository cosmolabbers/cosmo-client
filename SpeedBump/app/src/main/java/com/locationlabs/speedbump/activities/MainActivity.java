package com.locationlabs.speedbump.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.locationlabs.speedbump.utils.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends Activity {

    TextView jsonString;

    EventAdapter eventAdapter;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);

        if (LockDetectService.alertEvents != null && LockDetectService.alertEvents.size() != 0) {
            eventAdapter = new EventAdapter(LockDetectService.alertEvents);
            listview.setAdapter(eventAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent in = new Intent(this, LockDetectService.class);
        startService(in);

    }

    public class EventAdapter extends BaseAdapter {

        ArrayList<AlertEvent> alertEvents;

        EventAdapter(ArrayList<AlertEvent> alertEvents) {
            this.alertEvents = alertEvents;
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.eventName.setText(alertEvents.get(position).getEventName());
            holder.date.setText(alertEvents.get(position).getDisplayTime());

            return convertView;
        }
    }

    private class ViewHolder {
        TextView eventName, date;

    }

}
