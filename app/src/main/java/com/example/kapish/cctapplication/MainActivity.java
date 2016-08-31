package com.example.kapish.cctapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    static String PACKAGE_NAME = "chrome_package";

    final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    final String bbcURL = "http://www.bbc.co.uk/news/";
    final String googleURL = "http://google.com/";
    final static String EXTRA_START_BUNDLE = "extra_start_bundle";
    final static String EXTRA_URL = "extra_url";
    final static String SHIM = "com.example.kapish.cctapplication.ShimActivity";

    CustomTabsClient mCustomTabsClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    CustomTabsIntent mCustomTabsIntent;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(
                    ComponentName componentName, CustomTabsClient customTabsClient) {
                mCustomTabsClient= customTabsClient;
                mCustomTabsClient.warmup(0L);
                mCustomTabsSession = mCustomTabsClient.newSession(null);
                PACKAGE_NAME = componentName.getPackageName();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCustomTabsClient= null;
            }
        };

        CustomTabsClient.bindCustomTabsService(
                this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        mCustomTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession)
                .setShowTitle(true)
                .build();

        // Modify intent..
        mIntent = mCustomTabsIntent.intent;
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    }

    public void chromeCustomTabExample(View view) {
        mIntent.putExtra(EXTRA_START_BUNDLE, mCustomTabsIntent.startAnimationBundle);
        mIntent.putExtra(EXTRA_URL, bbcURL);
        mIntent.setClassName(this, SHIM);
        startActivity(mIntent);
    }

    public void chromeCustomTabGoogle(View view) {
        mIntent.putExtra(EXTRA_START_BUNDLE, mCustomTabsIntent.startAnimationBundle);
        mIntent.putExtra(EXTRA_URL, googleURL);
        mIntent.setClassName(this, SHIM);
        startActivity(mIntent);
    }
}
