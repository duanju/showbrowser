package com.dhms.tvshow;

import android.text.TextUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BrowserActivity extends FragmentActivity {
    public static final String URL = "Browser_Activity_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        String url = getIntent().getStringExtra(BrowserActivity.URL);
        Log.d("LastAccessUrl", "onCreate get Url " + url);
        if (!TextUtils.isEmpty(url)) {
            View frameLayout = findViewById(R.id.browser_root);
            getSupportFragmentManager().beginTransaction().replace(R.id.browser_root
                    , BrowserFragment.newInstance(url)).commit();
        } else {
            Toast.makeText(getApplicationContext()
                    , "Please specific URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}