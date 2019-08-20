package com.example.itunestest.Model;

import java.util.List;

public interface IAlbumsModel {

    List<Album> getAlbumList(String albumName);

    Album getAlbum(Integer albumId);

}
