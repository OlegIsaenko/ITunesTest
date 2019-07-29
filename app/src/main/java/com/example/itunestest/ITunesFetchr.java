package com.example.itunestest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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

class ITunesFetchr {

    private static final String TAG = "fetchr";
    private static boolean isToast;


    /*
        метод создает соединение используя URL, передаваемый в качестве параметра.
     */
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
        }
        finally {
            connection.disconnect();
        }
    }


    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


    /*
        метод делает запрос по названию альбома,
        создает и заполняет список объектов album из json ответа.
     */
    public List<Album> fetchAlbums(String albumName) {

        if (albumName.equals("")) {
            return null;
        }

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
            Type listType = new TypeToken<List<Album>>() {}.getType();
            albums = gson.fromJson(jsonArray, listType);
        }
        catch (IOException e) {
            Log.e(TAG, "fetchItems: ", e);
        }
        return albums;
    }

    /*
        метод делает запрос по ID альбома, из json ответа создает объект album
        и заполняет список объектов song. объектом json ответа с индексом [0] идет сам альбом,
        далее с индекса [1] - сами треки.
     */
    public Album fetchAlbum(String albumId) {
        Album album = new Album();
        try {
            String url = Uri.parse("https://itunes.apple.com/lookup")
                    .buildUpon()
                    .appendQueryParameter("id", albumId)
                    .appendQueryParameter("entity", "song")
                    .build().toString();

            Log.i(TAG, "fetchAlbum: " + url);

            String jsonString = getUrlString(url);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
            JsonArray jsonArray = jsonObject.getAsJsonArray("results");

            album = new Gson().fromJson(jsonArray.get(0), Album.class);
            jsonArray.remove(0);

            Type listType = new TypeToken<List<Song>>() {}.getType();
            List<Song> songs = new Gson().fromJson(jsonArray, listType);

            album.setSongs(songs);

        } catch (IOException e) {
            Log.e(TAG, "fetchItems: ", e);
        }
        return album;
    }

    //метод проверяет состояние интернет соединения.
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            Log.i(TAG, "isOnline: connection OK");
            return true;
        }
        Log.i(TAG, "isOnline: connection FAILED");
        lostConnectionToast(context);
        return false;
    }

    /*
        метод устанавливает задержку между отображением toast.
        нужен из-за того, что поле поиска реагирует на каждое изменение текста,
        и при отсутствии соединения помещает слишком много toast в очередь.
     */
    private static void lostConnectionToast(Context context) {
        if(!isToast) {
            Toast toast = Toast.makeText(context,
                    R.string.lost_connection, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            isToast = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isToast = false;
                }
            }, 2000);
        }
    }
}
