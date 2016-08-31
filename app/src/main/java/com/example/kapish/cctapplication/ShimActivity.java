package com.example.kapish.cctapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

public class ShimActivity extends Activity {

    private static final String TAG = "ChromeplateShim";

    static final String CHROME_CLASS = "com.google.android.apps.chrome.Main";

    private boolean mShouldFinishActivity;;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // onCreate() is called with a bundle when it's being recreated after being killed. This can
        // happen when the user presses back from CCT.
        if (savedInstanceState != null) {
            finish();
            return;
        }
        // Clone the intent and modify it for CCT.
        Intent chromeplateIntent = new Intent(getIntent());
        if (setupIntentForChromeplate(chromeplateIntent)) {
            chromeplateIntent.setData(
                    Uri.parse(chromeplateIntent.getStringExtra(MainActivity.EXTRA_URL)));
            chromeplateIntent.setComponent(
                    new ComponentName(
                            MainActivity.PACKAGE_NAME, "com.google.android.apps.chrome.Main"));
            ActivityCompat.startActivity(
                    this,
                    chromeplateIntent,
                    chromeplateIntent.getBundleExtra(MainActivity.EXTRA_START_BUNDLE));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Track calls to onStart() to know when to finish the activity. The first call happens when
        // CCT is first launched. Any subsequent call occurs when the CCT task is cleared, and
        // ChromeplateShim is next in the task stack. ChromeplateShim is just used to launch CCT and
        // should never be displayed by itself, so we call finish().
        if (mShouldFinishActivity) {
            finish();
        }
        mShouldFinishActivity = true;
    }

    @Override
    public void finish() {
        setVisible(false);
        super.finish();
        // Don't use any animation for this transition because ChromeplateShim activity should never be
        // seen by the user.
        overridePendingTransition(0, 0);
    }

    /** Sets up the given intent for CCT. Returns whether the intent could be configured. */
    private boolean setupIntentForChromeplate(Intent intent) {
        intent.setFlags(
                intent.getFlags()
                        & ~Intent.FLAG_ACTIVITY_NEW_TASK
                        & ~Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                        & ~Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        return true;
    }
}
