package com.dhms.tvshow;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhms.tvshow.db.BrowserSettings;

import java.util.List;


public class SearchActivity extends FragmentActivity {
    private final static int MESSAGE_ALL_HISTORY = 100;
    private static final Dpad D_PAD = new Dpad();
    private SearchView mSearchView;
    private RecyclerView mHistoryListView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchView = findViewById(R.id.search_search_box);
        mHistoryListView = findViewById(R.id.search_history_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mHistoryListView.setLayoutManager(layoutManager);
        mSearchView.setSubmitButtonEnabled(true);

        int goButtonId = mSearchView.getContext().getResources().getIdentifier("android:id/search_go_btn", null, null);
        View mGoButton = findViewById(goButtonId);
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence queryCharSeq = mSearchView.getQuery();
                if (!TextUtils.isEmpty(queryCharSeq)) {
                    String query = queryCharSeq.toString();
                    if (query.startsWith("s://")) {
                        // update settings
                        if(BrowserSettings.updateSettings(getApplicationContext(), query.substring(4))) {
                            mHistoryListView.getAdapter().notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Update WebView settings."
                                    , Toast.LENGTH_SHORT).show();
                        }

                        mSearchView.setQuery("", false);
                    } else {
                        String url = History.getValidateUrl(mSearchView.getQuery().toString());
                        startBrowserPage(SearchActivity.this, url);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please input url.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_ALL_HISTORY) {
                    if (null != msg.obj) {
                        final List<History> histories = (List<History>) msg.obj;
                        if (histories.size() > 0) {
                            // todo : check the thread issue here
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HistoryListAdapter adapter = new HistoryListAdapter(SearchActivity.this, histories);
                                    mHistoryListView.setAdapter(adapter);
                                }
                            });
                        }
                    }
                } else {
                    super.handleMessage(msg);
                }
            }
        };

        History.getAllSync(this, mHandler, MESSAGE_ALL_HISTORY);
    }

    private static void startBrowserPage(Context context, String url) {
        Intent startBrowser = new Intent(context, BrowserActivity.class);
        startBrowser.putExtra(BrowserActivity.URL, url);
        context.startActivity(startBrowser);
    }

    private static class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.HistoryViewHolder> {
        private Context mContext;
        private List<History> mHistories;
        RecyclerView.LayoutParams mLayoutParams;

        public HistoryListAdapter(Context context, List<History> histories) {
            super();
            this.mContext = context;
            this.mHistories = histories;
            this.mLayoutParams = new RecyclerView.LayoutParams(
                    (int) mContext.getResources().getDimension(R.dimen.history_item),
                    (int) mContext.getResources().getDimension(R.dimen.history_item)
            );
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HistoryViewHolder(((Activity) mContext).getLayoutInflater()
                    .inflate(R.layout.item_history, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            final History history = mHistories.get(position);
            try {
                holder.mFavImage.setImageBitmap(history.getFavBitmap());
            } catch (Exception ex) {
                // ignore
            }
            holder.mTitleView.setText(history.getDiscritption());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startBrowserPage(mContext, history.getUrl());
                }
            });
        }

        @Override
        public int getItemCount() {
            if (null != mHistories) {
                int maxHistoryCount = Integer.parseInt(BrowserSettings.getWebViewSettings(mContext,
                        BrowserSettings.KEY_OF_HISTORY_COUNT, "1024"));
                return Math.min(maxHistoryCount ,mHistories.size());
            } else {
                return 0;
            }
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            public ImageView mFavImage;
            public TextView mTitleView;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                this.mFavImage = itemView.findViewById(R.id.item_history_fav);
                this.mTitleView = itemView.findViewById(R.id.item_history_title);
            }
        }
    }
}