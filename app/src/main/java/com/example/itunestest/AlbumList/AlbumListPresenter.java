package com.example.itunestest.AlbumList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.itunestest.Model.Album;
import com.example.itunestest.Model.AlbumsModel;
import com.example.itunestest.Model.IAlbumsModel;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumListPresenter implements IAlbumListPresenter {

    public static final String TAG = "presenter";

    private IAlbumListView mAlbumListView;
    private IAlbumsModel mAlbumListModel;


    public AlbumListPresenter(IAlbumListView view) {
        mAlbumListView = view;
        mAlbumListModel = new AlbumsModel();
    }

    @Override
    public void getAlbumsFromModel(String albumName) {

        Observable.create((ObservableOnSubscribe<List<Album>>) subscriber -> {
            subscriber.onNext(mAlbumListModel.getAlbumList(albumName));
        })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(albumList -> {
                  if (albumList != null && !albumList.isEmpty()) {
                      Collections.sort(albumList, Album.ALPHABETICAL_ORDER);
                      mAlbumListView.showAlbums(albumList);
                  }
                  else mAlbumListView.showError();
              });
    }

    @Override
    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.i(TAG, "isOnline: connection OK");
            return true;
        }
        return false;
    }
}
