package com.dhms.tvshow;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SearchActivity extends FragmentActivity {
    private static final Dpad D_PAD= new Dpad();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        int direction = D_PAD.getDirectionPressed(event);
        Log.d("DPad", "onKeyDown: "+ direction);

        return super.onGenericMotionEvent(event);
    }
}