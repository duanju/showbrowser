package com.dhms.tvshow;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhms.tvshow.db.BrowserSettings;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class BrowserFragment extends Fragment {
    private static String sUrl;

    static BrowserFragment newInstance(String url) {
        sUrl = url;
        return new BrowserFragment();
    }

    private WebView mWebView;
    private History mHistory;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mHistory = new History(sUrl);
        updateHistoryUrl(true);
        View rootView = inflater.inflate(R.layout.browser_fragment, container, false);

        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(false);
        mWebView.getSettings().setUseWideViewPort(false);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBlockNetworkImage(false);

        int webScale = 100;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int nScreenHeight = displaymetrics.heightPixels;
        String scale = BrowserSettings.getWebViewSettings(
                Objects.requireNonNull(getContext()), BrowserSettings.KEY_OF_WEB_VIEW_SCALE, "50");
        Log.d("WebviewSettings", "scale: " + scale);
        webScale = (int) Math.round((nScreenHeight / 540.0) * Integer.parseInt(scale));
        mWebView.setVisibility(View.INVISIBLE);
        mWebView.setInitialScale(webScale);
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("LastAccessUrl", "shouldOverrideUrlLoading: " + url);
                mHistory.setUrl(url);
                updateHistoryUrl(false);
                return false; //Allow WebView to load url
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                Log.d("LastAccessUrl", "onReceivedTitle: " + title);
                mHistory.setTitle(mWebView.getTitle());
                updateHistoryUrl(false);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                mHistory.setFavIconData(compress(icon));
                updateHistoryUrl(false);

                Log.d("LastAccessUrl", "onReceivedIcon: ");

                super.onReceivedIcon(view, icon);
            }

            private byte[] compress(Bitmap bmp) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                return byteArray;
            }
        });
        mWebView.loadUrl(sUrl);

        return rootView;
    }

    private void updateHistoryUrl(boolean add) {
        if (null != getActivity()) {
            Log.d("LastAccessUrl", "updateHistoryUrl: " + mHistory.getUrl());
            Log.d("LastAccessUrl", "updateHistory Title: " + mHistory.getTitle());
            if (add) {
                History.saveSync(getActivity(), mHistory);
            } else {
                History.updateSync(getActivity(), mHistory);
            }
        }
    }

    @Override
    public void onStop() {
        mWebView.stopLoading();
        mWebView.destroy();
        super.onStop();
    }
}
