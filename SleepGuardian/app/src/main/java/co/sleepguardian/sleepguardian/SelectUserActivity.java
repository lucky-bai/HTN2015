package co.sleepguardian.sleepguardian;

import android.app.Activity;
import android.content.Intent;
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
import org.json.JSONObject;

public class SelectUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        setTitle("Select User");

        final SharedPreferences settings = getSharedPreferences(Common.PREFS_NAME, 0);

        final TextView userNameTextView = (TextView) findViewById(R.id.username_edittext);
        final TextView fullNameTextView = (TextView) findViewById(R.id.fullname_edittext);
        final Button saveUserNameButton = (Button) findViewById(R.id.save_username);

        saveUserNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = userNameTextView.getText().toString();
                if (username.equals("")) {
                    Common.makeToast(getApplicationContext(), "You must enter a username.");
                    return;
                }

                String fullName = fullNameTextView.getText().toString();
                signUpOrSignIn(username, fullName);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Common.USERNAME_PREF_KEY, username);
                editor.apply();
                Common.makeToast(getApplicationContext(), "Username " + username + " saved!");

                goToUploadActivity();
            }
        });
    }

    private void signUpOrSignIn(String username, final String fullName) {
        String apiUrl = Common.HOST;
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("username", username);
            userInfo.put("full_name", fullName);
        } catch(Exception e) {

        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, apiUrl, userInfo.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        Common.makeToast(getApplicationContext(), "Welcome " + fullName + "!");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                });
        Volley.newRequestQueue(this).add(jsObjRequest);
    }

    private void goToUploadActivity() {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_user, menu);
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
