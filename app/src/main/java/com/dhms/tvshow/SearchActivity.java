package com.dhms.tvshow;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;


public class SearchActivity extends FragmentActivity {
    private static final Dpad D_PAD = new Dpad();
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchView = findViewById(R.id.search_search_box);
        mSearchView.setSubmitButtonEnabled(true);

        int goButtonId = mSearchView.getContext().getResources().getIdentifier("android:id/search_go_btn", null, null);
        View mGoButton = findViewById(goButtonId);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                    String url = History.getValidateUrl(mSearchView.getQuery().toString());
                    Intent startBrowser = new Intent(SearchActivity.this, BrowserActivity.class);
                    startBrowser.putExtra(BrowserActivity.URL, url);
                    startActivity(startBrowser);
                } else {
                    Toast.makeText(getApplicationContext(), "Please input url.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}