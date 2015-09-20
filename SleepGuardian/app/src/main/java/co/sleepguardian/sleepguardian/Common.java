package co.sleepguardian.sleepguardian;

import android.content.Context;
import android.widget.Toast;

public class Common {

    public static final String PREFS_NAME = "SleepGuardianPrefs";
    public static final String USERNAME_PREF_KEY = "username";
    public static final String HOST = "http://www.sleepguardian.co";

    public static void makeToast(Context context, CharSequence text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
