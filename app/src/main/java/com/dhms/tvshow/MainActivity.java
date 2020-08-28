/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dhms.tvshow;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.tencent.smtt.sdk.QbSdk;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {
    private final static int MESSAGE_LASTER_HISTORY = 100;
    private static boolean sInited = false;
    @Override
    protected void onStart() {
        super.onStart();
        final Handler handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_LASTER_HISTORY) {
                    History last = (History) msg.obj;
                    if (null == last || TextUtils.isEmpty(last.getUrl())) {
                        Intent startBrowser = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(startBrowser);
                    } else {
                        Intent startBrowser = new Intent(MainActivity.this, BrowserActivity.class);
                        startBrowser.putExtra(BrowserActivity.URL, last.getUrl());
                        startActivity(startBrowser);
                    }

                    finish();
                } else {
                    super.handleMessage(msg);
                }
            }
        };

        final TextView messageView = (TextView) findViewById(R.id.main_loading_x5);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                if (arg0) {
                    sInited = true;
                    messageView.setText(getString(R.string.success_load_x5_web_view
                            , QbSdk.getTbsVersion(getApplicationContext())));
                    History.getLastSync(getApplicationContext(), handler, MESSAGE_LASTER_HISTORY);
                } else {
                    messageView.setText(getString(R.string.fail_load_x5_web_view));
                }
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };

        if (sInited) {
            History.getLastSync(getApplicationContext(), handler, MESSAGE_LASTER_HISTORY);
        } else {
            QbSdk.initX5Environment(getApplicationContext(), cb);
            messageView.setText(getText(R.string.loading_x5_webview));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
