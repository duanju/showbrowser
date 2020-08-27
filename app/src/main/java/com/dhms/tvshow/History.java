package com.dhms.tvshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dhms.tvshow.db.HistoryDao;
import com.dhms.tvshow.db.ShareBrowserDatabase;

import java.util.List;
import java.util.ListIterator;

@Entity(tableName = "histories")
public class History {
    // MAX_HISTORY_NUMBER MUST BE LARGER THEN 1
    public static final int MAX_HISTORY_NUMBER = 6;
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String url;
    private String auth;
    private String title;
    private byte[] favIconData;

    public History(String url) {
        this.url = url;
        this.auth = Uri.parse(url).getAuthority();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.auth = Uri.parse(url).getAuthority();
    }

    public String getAuth() {
        return this.auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getFavIconData() {
        return this.favIconData;
    }

    public Bitmap getFavBitmap() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(this.favIconData, 0, this.favIconData.length);
        return bitmap;
    }

    public void setFavIconData(byte[] favIconData) {
        this.favIconData = favIconData;
    }

    public String getDiscritption() {
        String desc = title;
        if (TextUtils.isEmpty(desc)) {
            desc = auth;
        }

        if (TextUtils.isEmpty(desc)) {
            desc = url;
        }

        return desc;
    }

    public static void getAllSync(final Context context, final Handler handler, final int what) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ShareBrowserDatabase database = ShareBrowserDatabase.createDB(context);
                List<History> all = database.userDao().loadAll();
                Message message = handler.obtainMessage();
                message.what = what;
                message.obj = all;

                handler.dispatchMessage(message);
            }
        });

        thread.start();
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
                    ListIterator<History> iterator = all.listIterator();
                    while (iterator.hasNext()) {
                        History h = iterator.next();
                        if (h.url.equals(history.url)) {
                            dao.delete(h);
                            history.setTitle(h.getTitle());
                            history.setFavIconData(h.getFavIconData());
                            iterator.remove();
                        }
                    }
                    // remove the first earliest history if the total history count is larger than the
                    // max value
                    if (all.size() >= MAX_HISTORY_NUMBER) {
                        dao.delete(all.get(0));
                    }
                }

                history.setId(dao.insert(history));
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
