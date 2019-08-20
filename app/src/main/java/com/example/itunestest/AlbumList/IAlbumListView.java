package com.example.itunestest.AlbumList;

import com.example.itunestest.Model.Album;

import java.util.List;

public interface IAlbumListView {

    void showAlbums(List<Album> albums);

    void showError();
}
