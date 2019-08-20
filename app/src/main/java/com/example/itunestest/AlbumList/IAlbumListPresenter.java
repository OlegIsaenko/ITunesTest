package com.example.itunestest.AlbumList;

import android.content.Context;

public interface IAlbumListPresenter {
    void getAlbumsFromModel(String albumName);

    boolean isOnline(Context context);
}
