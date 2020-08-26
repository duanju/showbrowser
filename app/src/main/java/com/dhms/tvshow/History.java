package com.dhms.tvshow;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dhms.tvshow.db.HistoryDao;
import com.dhms.tvshow.db.ShareBrowserDatabase;

import java.util.Date;
import java.util.List;

@Entity(tableName = "histories")
public class History {
    // MAX_HISTORY_NUMBER MUST BE LARGER THEN 1
    public static final int MAX_HISTORY_NUMBER = 5;
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String url;

    public History(String url) {
        this.url = url;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static void getLastSync(final Context context, final Handler handler, final int what) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ShareBrowserDatabase database = ShareBrowserDatabase.createDB(context);
                List<History> all = database.userDao().loadAll();
                Message message = handler.obtainMessage();
                message.what = what;
                if (null != all && all.size() > 0) {
                    message.obj = all.get(all.size() - 1);
                } else {
                    message.obj = null;
                }

                handler.dispatchMessage(message);
            }
        });

        thread.start();
    }

    public static void saveSync(final Context context, final History history) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ShareBrowserDatabase database = ShareBrowserDatabase.createDB(context);
                HistoryDao dao = database.userDao();
                List<History> all = dao.loadAll();
                if (all != null) {
                    // remove the duplicated history from db
                    for (History h : all) {
                        if (h.url.equals(history.url)) {
                            dao.delete(h);
                        }
                    }

                    // remove the first earliest history if the total history count is larger than the
                    // max value
                    if (all.size() >= MAX_HISTORY_NUMBER) {
                        dao.delete(all.get(0));
                    }
                }

                dao.insert(history);
            }
        });

        thread.start();
    }

    public static void updateSync(final Context context, final History history) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ShareBrowserDatabase database = ShareBrowserDatabase.createDB(context);
                HistoryDao dao = database.userDao();
                dao.update(history);
            }
        });

        thread.start();
    }

    public static String getValidateUrl(String url) {
        if (TextUtils.getTrimmedLength(url) > 0) {
            url = url.trim();
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            return url;
        }

        return null;
    }
}
