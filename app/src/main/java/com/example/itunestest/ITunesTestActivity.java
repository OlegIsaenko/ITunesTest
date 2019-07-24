package com.example.itunestest;

import android.support.v4.app.Fragment;

public class ITunesTestActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ITunesTestFragment.newInstance();
    }
}
