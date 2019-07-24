package com.example.itunestest;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ITunesFetchr {

    private static final String TAG = "hyeg";


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Album> fetchAlbums(String albumName) {
        List<Album> albums = new ArrayList<>();
        try {
            String url = Uri.parse("https://itunes.apple.com/search")
                    .buildUpon()
                    .appendQueryParameter("term", albumName)
                    .appendQueryParameter("media", "music")
                    .appendQueryParameter("entity", "album")
                    .appendQueryParameter("attribute", "albumTerm")
                    .build().toString();
            Log.i(TAG, "fetchItems: " + url);
            String jsonString = getUrlString(url);
//            ----------------------------------------------------------
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Album>>() {}.getType();
            albums = gson.fromJson(jsonArray, listType);
            Log.i(TAG, "parseAlbums: GSON: " + albums.get(2) + " \n" + albums.toString());
//            ----------------------------------------------------------
//            parseAlbums(albums, jsonString);
        } catch (IOException e) {
            Log.e(TAG, "fetchItems: ", e);
        }
//        catch (JSONException je) {
//            Log.e(TAG, "fetchItems: ", je);
//        }
        return albums;
    }

//    private void parseAlbums(List<Album> albums, String jsonString)
//            throws JSONException{
//
//        JSONObject jsonBody = new JSONObject(jsonString);
//        JSONArray albumJsonArray = jsonBody.getJSONArray("results");
//
//        for (int i = 0; i < albumJsonArray.length(); i++) {
//            JSONObject albumJsonObject = albumJsonArray.getJSONObject(i);
//            Album albumItem = getAlbumFromJson(albumJsonObject);
//            albums.add(albumItem);
//        }
//    }

    private Album getAlbumFromJson(JSONObject albumJsonObject) throws JSONException {
        Album album = new Album();
        album.setCollectionId(albumJsonObject.getString("collectionId"));
        album.setCollectionName(albumJsonObject.getString("collectionName"));
        album.setArtistName(albumJsonObject.getString("artistName"));
        album.setCollectionViewUrl(albumJsonObject.getString("collectionViewUrl"));
        album.setCover(albumJsonObject.getString("artworkUrl60"));
        album.setCollectionPrice(albumJsonObject.getString("collectionPrice"));
        album.setTrackCount(albumJsonObject.getString("trackCount"));
        album.setCopyright(albumJsonObject.getString("copyright"));
        album.setCountry(albumJsonObject.getString("country"));
        album.setCurrency(albumJsonObject.getString("currency"));
        album.setReleaseDate(albumJsonObject.getString("releaseDate"));
        album.setPrimaryGenreName(albumJsonObject.getString("primaryGenreName"));
        return album;
    }

    public Album fetchAlbum(String albumId) {

        Album album = new Album();
        try {
            String url = Uri.parse("https://itunes.apple.com/lookup")
                    .buildUpon()
                    .appendQueryParameter("id", albumId)
                    .appendQueryParameter("entity", "song")
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            album = parseSongs(album, jsonBody);
        } catch (IOException e) {
            Log.e(TAG, "fetchItems: ", e);
        }

        catch (JSONException je) {
            Log.e(TAG, "fetchItems: ", je);
        }
        Log.i(TAG, "ALBUM FROM SONGS: " + album);
        return album;
    }



    private Album parseSongs(Album album, JSONObject jsonBody)
            throws  IOException, JSONException {

        JSONArray albumJsonArray = jsonBody.getJSONArray("results");
        JSONObject jsonAlbum = albumJsonArray.getJSONObject(0);
        album = getAlbumFromJson(jsonAlbum);

        for (int i = 1; i < albumJsonArray.length(); i++) {
            JSONObject albumJsonObject = albumJsonArray.getJSONObject(i);
            Song song = new Song();
            song.setTrackId(albumJsonObject.getString("trackId"));
            song.setTrackName(albumJsonObject.getString("trackName"));
            song.setArtistName(albumJsonObject.getString("artistName"));
            song.setCollectionName(albumJsonObject.getString("collectionName"));
            album.getSongs().add(song);
        }
        return album;
    }
}
