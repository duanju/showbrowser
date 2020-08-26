package com.dhms.tvshow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class BrowserFragment extends Fragment {

    private static final String KEY_OF_LAST_ACCESS_URL = "Last_Access_Url";
    private static String sUrl;

    static BrowserFragment newInstance(String url) {
        sUrl = url;
        return new BrowserFragment();
    }

    private Activity mActivity;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        updateLastAccessUrl(mActivity, sUrl);

        View rootView = inflater.inflate(R.layout.browser_fragment, container, false);

        WebView webView = rootView.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);

        int webScale = 100;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int nScreenHeight = displaymetrics.heightPixels;
        webScale = (int) Math.round((nScreenHeight / 540.0) * 50.0);
        webView.setVisibility(View.INVISIBLE);
        webView.setInitialScale(webScale);
        webView.setVisibility(View.VISIBLE);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d("LastAccessUrl", "shouldOverrideUrlLoading: " + url);
//                updateLastAccessUrl(mActivity, url);
//
//                return false; //Allow WebView to load url
//            }
//        });

        webView.loadUrl(sUrl);

        return rootView;
    }

    private static void updateLastAccessUrl(@Nullable Activity activity, String url) {
        if (null != activity) {
            History history = new History(url);
            History.saveSync(activity, history);
        }
    }
}
