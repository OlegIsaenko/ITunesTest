package com.example.itunestest.Album;

import com.example.itunestest.Model.Album;
import com.example.itunestest.Model.AlbumsModel;
import com.example.itunestest.Model.IAlbumsModel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlbumPresenter implements IAlbumPresenter {

    private IAlbumView mAlbumView;
    private IAlbumsModel mAlbumModel;
    private Album mAlbum;

    public AlbumPresenter(IAlbumView view) {
        mAlbumView = view;
        mAlbumModel = new AlbumsModel();
    }

    @Override
    public void getAlbum(int albumId) {
        Observable.fromCallable(() -> mAlbumModel.getAlbum(albumId)
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(album -> {
                    if (album != null || album.getCollectionId() == 0) {
                        mAlbumView.showAlbum(album);
                    } else {
                        mAlbumView.showError();
                    }
                    mAlbum = album;
                });
    }

    @Override
    public String getAlbumUrl() {
        return mAlbum.getCollectionViewUrl();
    }
}
