package com.dhms.tvshow.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class BrowserSettings {
    private static final String NAME_OF_WEB_VIEW_SETTING_SHARE_PREFERENCE = "browserSetting";
    public static final String KEY_OF_WEB_VIEW_SCALE = "scale";
    public static final String KEY_OF_HISTORY_COUNT = "history";

    public static String getWebViewSettings(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(NAME_OF_WEB_VIEW_SETTING_SHARE_PREFERENCE, 0);
        return sharedPreferences.getString(key, defValue);
    }

    public static boolean updateSettings(Context context, String settingsQuery) {
        boolean updated =false;
        if (!TextUtils.isEmpty(settingsQuery)) {
            String[] settingTerms = settingsQuery.split(",");
            for (String settingTerm : settingTerms) {
                if (!TextUtils.isEmpty(settingTerm)) {
                    String[] setting = settingTerm.split("=");
                    if (2==setting.length) {
                        String key = setting[0];
                        String value = setting[1];
                        saveWebViewSettings(context, key, value);
                        updated = true;
                    }
                }
            }
        }

        return updated;
    }

    private static void saveWebViewSettings(Context context, String key, String scale) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(NAME_OF_WEB_VIEW_SETTING_SHARE_PREFERENCE, 0);
        sharedPreferences.edit().putString(key, scale).apply();
    }
}
