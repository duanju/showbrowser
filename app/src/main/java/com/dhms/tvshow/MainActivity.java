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

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {
    private final static String BAIDU = "https://console.bce.baidu.com/bcd/#/bcd/manage/detail~domain=duanju91.top&resourceId=5a512559-124d-49f6-9aa3-2415adbd7677";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String lastAccessUrl = BrowserFragment.getLastAccessUrl(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (TextUtils.isEmpty(lastAccessUrl)) {
            fragmentManager.beginTransaction().replace(R.id.main_fragment
                    , BrowserFragment.newInstance(BAIDU)).commit();
        } else {
            Log.d("LastAccessUrl", lastAccessUrl);
            fragmentManager.beginTransaction().replace(R.id.main_fragment
                    , BrowserFragment.newInstance(lastAccessUrl)).commit();
        }
    }
}
