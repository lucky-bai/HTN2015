package co.sleepguardian.sleepguardian;

import android.app.Activity;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UploadActivity extends Activity {

    public static final String UPLOADED_UNTIL_KEY = "uploaded_until";
    public static final String USER = "user";
    public static final String TIMESTAMPS = "timestamps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        setTitle("Upload Activities");
        updateUploadedTime();

        final Button uploadButton = (Button) findViewById(R.id.upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            SharedPreferences settings = getSharedPreferences(Common.PREFS_NAME, 0);
            String username = settings.getString(Common.USERNAME_PREF_KEY, "");

            public void onClick(View v) {
                if (username.equals("")) {
                    Common.makeToast(getApplicationContext(), "You must enter a username before uploading!");
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
                updateUploadedTime();
            }
        });
    }

    private JSONArray getArrayAsJson(long uploadedUntil, long currentTimestamp) {
        UsageStatsManager userStatsManager = (UsageStatsManager) UploadActivity.this.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = userStatsManager.queryEvents(uploadedUntil, currentTimestamp);
        JSONArray jsonEvents = new JSONArray();
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
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
        String apiUrl = Common.HOST + "/" + USER + "/" + username + "/" + TIMESTAMPS;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, apiUrl, events.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        Common.makeToast(getApplicationContext(), "Uploaded successfully to server!");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });
        Volley.newRequestQueue(this).add(jsObjRequest);
    }

    private void updateUploadedTime() {
        TextView updatedTime = (TextView) findViewById(R.id.last_uploaded);
        String message;
        SharedPreferences settings = getSharedPreferences(Common.PREFS_NAME, 0);
        long uploadedUntil = settings.getLong(UPLOADED_UNTIL_KEY, 0);
        if (uploadedUntil == 0) {
            message = "You have never uploaded app usage statistics before. Tap on upload to upload now.";
        } else {
            Date date = new Date(uploadedUntil);
            DateFormat formatter = new SimpleDateFormat("yyyy-mm-d HH:mm:ss");
            String dateFormatted = formatter.format(date);
            message = "App usage statistics up to " + dateFormatted + " are uploaded. Tap on upload to upload data collected since.";
        }
        updatedTime.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
