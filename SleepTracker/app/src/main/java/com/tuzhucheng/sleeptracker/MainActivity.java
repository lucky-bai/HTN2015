package com.tuzhucheng.sleeptracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UsageStatsManager userStatsManager = (UsageStatsManager) MainActivity.this.getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long end = calendar.getTimeInMillis();
        long start = end - 2592000000L;  // Data for previous month

        UsageEvents usageEvents = userStatsManager.queryEvents(start, end);
        Map<String, String> events = new HashMap<>();
        while (usageEvents.hasNextEvent()) {
            Event event = new Event();
            usageEvents.getNextEvent(event);
            int eventType = event.getEventType();
            long eventTimestamp = event.getTimeStamp() / 1000;
            System.out.println(event.getClassName() + " " + eventTimestamp + " " + eventType);
            events.put(event.getClassName(), String.valueOf(event.getEventType()));
        }
        System.out.println(events.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
