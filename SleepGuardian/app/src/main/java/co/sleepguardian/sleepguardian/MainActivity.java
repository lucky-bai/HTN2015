package co.sleepguardian.sleepguardian;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageEvents.Event;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MainActivity extends Activity {

    public static final String PREFS_NAME = "SleepGuardianPrefs";
    public static final String USERNAME_PREF_KEY = "username";
    public static final String UPLOADED_UNTIL_KEY = "uploaded_until";

    public static final String HOST = "http://www.sleepguardian.co";
    public static final String USER = "user";
    public static final String TIMESTAMPS = "timestamps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        final TextView userNameTextView = (TextView) findViewById(R.id.username_edittext);
        final Button saveUserNameButton = (Button) findViewById(R.id.save_username);
        final Button uploadButton = (Button) findViewById(R.id.upload);

        userNameTextView.setText(settings.getString(USERNAME_PREF_KEY, ""));

        saveUserNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = userNameTextView.getText().toString();

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USERNAME_PREF_KEY, username);
                editor.apply();
                makeToast("Username " + username + " saved!");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String username = settings.getString(USERNAME_PREF_KEY, "");
            public void onClick(View v) {
                if (username.equals("")) {
                    makeToast("You must enter a username before uploading!");
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                long currentTimestamp = calendar.getTimeInMillis();
                long uploadedUntil = settings.getLong(UPLOADED_UNTIL_KEY, 0);
                JSONArray events = getArrayAsJson(uploadedUntil, currentTimestamp);
                upload(username, events);

                // Update uploadedUntil so next time we don't upload duplicate data
                SharedPreferences.Editor editor = settings.edit();
                editor.putLong(UPLOADED_UNTIL_KEY, currentTimestamp);
                editor.apply();
            }
        });
    }

    public void makeToast(CharSequence text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private JSONArray getArrayAsJson(long uploadedUntil, long currentTimestamp) {
        UsageStatsManager userStatsManager = (UsageStatsManager) MainActivity.this.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = userStatsManager.queryEvents(uploadedUntil, currentTimestamp);
        JSONArray jsonEvents = new JSONArray();
        while (usageEvents.hasNextEvent()) {
            Event event = new Event();
            usageEvents.getNextEvent(event);
            int eventType = event.getEventType();
            long eventTimestamp = event.getTimeStamp() / 1000;
            JSONObject jsonEvent = new JSONObject();
            try {
                jsonEvent.put("source", "android");
                jsonEvent.put("timestamp", eventTimestamp);
                jsonEvent.put("subject", event.getClassName());
                jsonEvent.put("event_type", eventType);
            } catch (JSONException e) {
                System.err.println("JSONException: " + e.getMessage());
            }
            jsonEvents.put(jsonEvent);
        }
        return jsonEvents;
    }

    private void upload(String username, JSONArray events) {
        String apiUrl = HOST + "/" + USER + "/" + username + "/" + TIMESTAMPS;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, apiUrl, events.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        makeToast("Uploaded successfully to server!");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });
        Volley.newRequestQueue(this).add(jsObjRequest);
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
