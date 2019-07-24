package com.example.itunestest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Album {

    private String collectionId;
    private String collectionName;
    private String artistName;
    private String collectionViewUrl;
    private String artistViewUrl;
    private String artworkUrl60;
    private String collectionPrice;
    private String trackCount;
    private String copyright;
    private String country;
    private String currency;
    private String releaseDate;
    private String primaryGenreName;
    private List<Song> mSongs = new ArrayList<>();

    @Override
    public String toString() {
        return  collectionName + "\n" +
                artistName + "\n" +
                collectionViewUrl + "\n" +
                mSongs.size()
                ;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCollectionViewUrl() {
        return collectionViewUrl;
    }

    public void setCollectionViewUrl(String collectionViewUrl) {
        this.collectionViewUrl = collectionViewUrl;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public void setArtworkUrl60(String artworkUrl60) {
        this.artworkUrl60 = artworkUrl60;
    }

    public String getArtistViewUrl() {
        return artistViewUrl;
    }

    public void setArtistViewUrl(String artistViewUrl) {
        this.artistViewUrl = artistViewUrl;
    }

    public String getCollectionPrice() {
        return collectionPrice;
    }

    public void setCollectionPrice(String collectionPrice) {
        this.collectionPrice = collectionPrice;
    }

    public String getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(String trackCount) {
        this.trackCount = trackCount;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPrimaryGenreName() {
        return primaryGenreName;
    }

    public void setPrimaryGenreName(String primaryGenreName) {
        this.primaryGenreName = primaryGenreName;
    }

    public List<Song> getSongs() {
        return mSongs;
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    public static Comparator<Album> ALPHABETICAL_ORDER = new Comparator<Album>() {
        @Override
        public int compare(Album o1, Album o2) {
            String thisName = o1.getCollectionName();
            String objName = o2.getCollectionName();
            int res = String.CASE_INSENSITIVE_ORDER.compare(thisName, objName);
            if (res == 0) {
                res = thisName.compareTo(objName);
            }
            return res;
        }
    };
}
