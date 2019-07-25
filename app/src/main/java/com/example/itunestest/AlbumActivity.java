package com.example.itunestest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class AlbumActivity extends AppCompatActivity {

    public static final String TAG = "tag";
    private String albumId;
//    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
//        mToolbar = findViewById(R.id.toolbar);

        albumId = getIntent().getStringExtra(AlbumFragment.ALBUM_ID);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.album_container);

        if (fragment == null) {
            fragment = AlbumFragment.newInstance(albumId);
            manager.beginTransaction()
                    .add(R.id.album_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
