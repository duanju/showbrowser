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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {
    private final static String BAIDU = "https://console.bce.baidu.com/bcd/#/bcd/manage/detail~domain=duanju91.top&resourceId=5a512559-124d-49f6-9aa3-2415adbd7677";
    private final static int MESSAGE_LASTER_HISTORY = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_LASTER_HISTORY) {
                    History last = (History) msg.obj;
                    if (null == last || TextUtils.isEmpty(last.getUrl())) {
//        if (true) {
                        Intent startBrowser = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(startBrowser);
                    } else {
                        Intent startBrowser = new Intent(MainActivity.this, BrowserActivity.class);
                        startBrowser.putExtra(BrowserActivity.URL, BAIDU);
                        startActivity(startBrowser);
                    }
                } else {
                    super.handleMessage(msg);
                }

            }
        };

        History.getLastSync(this, handler, MESSAGE_LASTER_HISTORY);
    }
}
