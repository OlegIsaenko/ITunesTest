package com.example.itunestest.Model;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumsModel implements IAlbumsModel {

    private static final String TAG = "presenter";

    @Override
    public List<Album> getAlbumList(String albumName) {

        List<Album> albums = new ArrayList<>();
        String url = Uri.parse("https://itunes.apple.com/search")
                .buildUpon()
                .appendQueryParameter("term", albumName)
                .appendQueryParameter("media", "music")
                .appendQueryParameter("entity", "album")
                .appendQueryParameter("attribute", "albumTerm")
                .appendQueryParameter("limit", "100")
                .build().toString();
        try {
            String jsonString = getUrlString(url);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Album>>() {
            }.getType();
            albums = gson.fromJson(jsonArray, listType);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "fetchItems: ERROR ALBUMS ", e);
        }
        return albums;
    }

    @Override
    public Album getAlbum(Integer albumId) {
        Album album = new Album();
        try {
            String url = Uri.parse("https://itunes.apple.com/lookup")
                    .buildUpon()
                    .appendQueryParameter("id", albumId.toString())
                    .appendQueryParameter("entity", "song")
                    .build().toString();


            String jsonString = getUrlString(url);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");

            album = new Gson().fromJson(jsonArray.get(0), Album.class);
            jsonArray.remove(0);

            Type listType = new TypeToken<List<Song>>() {
            }.getType();
            List<Song> songs = new Gson().fromJson(jsonArray, listType);

            album.setSongs(songs);

        } catch (IOException e) {
            Log.e(TAG, "fetchItems: ERROR ", e);
        }
        return album;
    }


    private byte[] getUrlBytes(String urlSpec) throws IOException {
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
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }


    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
