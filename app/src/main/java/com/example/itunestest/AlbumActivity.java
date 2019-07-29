package com.example.itunestest;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AlbumActivity extends AppCompatActivity {

    public static final String ALBUM_ID = "album_id";
    private static final String TAG = "tag";
    private String albumId;
    private Album mAlbum;
    private ImageView mAlbumCover;
    private RecyclerView mAlbumRecyclerView;
    private Toolbar mToolbar;
    private LinearLayout mAlbumInfo;
    private boolean infoInvisible = true;

    private TextView albumName;
    private TextView artistName;
    private TextView albumDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        albumId = getIntent().getStringExtra(ALBUM_ID);

        albumName = findViewById(R.id.album_name);
        artistName = findViewById(R.id.artist_name);
        albumDescription = findViewById(R.id.album_description);

        mToolbar = findViewById(R.id.album_toolbar);

        /*
            Детальная информация об альбоме. Видимость поверх обложки альбома
            меняется при нажатии иконки "i" в меню (R.id.album_info).
            метод onClick передает в браузер ссылку на альбом с https://music.apple.com
         */
        mAlbumInfo = findViewById(R.id.information_layout);
        mAlbumInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAlbum.getCollectionViewUrl()));
                startActivity(intent);
            }
        });

        mAlbumCover = findViewById(R.id.new_cover_view);
        mAlbumRecyclerView = findViewById(R.id.album_songs_recycler_view);
        mAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(AlbumActivity.this));
        new FetchSongsTask().execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu, menu);
        return true;
    }

    /*
        Управляет видимостью макета с информацией об альбоме (R.id.information_layout)
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.album_info:
                if (infoInvisible) {
                    mAlbumInfo.setVisibility(View.VISIBLE);
                    infoInvisible = !infoInvisible;
                } else  {
                    mAlbumInfo.setVisibility(View.INVISIBLE);
                    infoInvisible = !infoInvisible;
                }
                return true;

                default:
                    super.onOptionsItemSelected(item);
        }
        return false;
    }

    //класс создает фоновый поток, в котором делает запрос по параметру albumId.
    //по окончанию выполнения обновляет макеты альбома.
    private class FetchSongsTask extends AsyncTask<Void, Void, Album> {

        @Override
        protected Album doInBackground(Void... voids) {
            return new ITunesFetchr().fetchAlbum(albumId);
        }

        @Override
        protected void onPostExecute(Album album) {
            mAlbum = album;
            updateUI();
        }
    }

    /*
        Метод заполняет часть макета информацией об альбоме:
        - название альбома устанавливается в toolbar;
        - подготавливается строка запроса для обложки альбома
            (по умолчанию itunes отдает ссылки на обложки с размерами 60х60 и 100х100,
             хотя хранит их и в более высоком разрешении. Меняем в строке на нужный размер.)
        - с помощью Picasso загружается и устанавливается обложка в макет.
     */
    private void updateUI() {
        if (mAlbum == null) {
            Toast.makeText(this, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        mToolbar.setTitle(mAlbum.getCollectionName());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String cover = mAlbum.getArtworkUrl60()
                .replace("60x60", "600x600");
        Picasso.get().load(cover).into(mAlbumCover);
        updateAlbumDescription();
        mAlbumRecyclerView.setAdapter(new SongAdapter(mAlbum));
    }

    /*
        метод заполняет макет с детальной информацией об альбоме
     */
    private void updateAlbumDescription() {
        albumName.setText(mAlbum.getCollectionName());
        artistName.setText(mAlbum.getArtistName());

        /*
            в зависимости от количества треков подбираем окончание
            "track(-s)" или "трек(-а/-ов)".
         */
        String count = getResources().getString(R.string.songs);
        int i = Integer.parseInt(mAlbum.getTrackCount());
        if (i == 1) {
            count = getResources().getString(R.string.song);
        } else if (i % 10 > 1 && i % 10 < 5 && i != 11 && i != 12 && i != 13 && i != 14) {
            count = getResources().getString(R.string.song4);
        }

        //преобразуем строку с датой в объект Date, затем обратно в строку с форматированием.
        String dateStr = mAlbum.getReleaseDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = format.parse(dateStr);
            dateStr = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            Log.e(TAG, "updateAlbumDescription: ", e);
        }

        //заполняем макет обработанной информацией.
        String description = mAlbum.getPrimaryGenreName() + ", " + mAlbum.getCountry() + "\n" +
                        mAlbum.getTrackCount() + " " + count + "\n" +
                        mAlbum.getCollectionPrice() + " " + mAlbum.getCurrency() + "\n" +
                        getResources().getString(R.string.released_date) + " " +
                        dateStr + "\n\n" + mAlbum.getCopyright();
        albumDescription.setText(description);
    }

    private class SongHolder extends RecyclerView.ViewHolder {

        private TextView songName;
        private TextView songNumber;
        private TextView songLength;

        SongHolder(View albumView) {
            super(albumView);
            songName = albumView.findViewById(R.id.song_name);
            songNumber = albumView.findViewById(R.id.song_number);
            songLength = albumView.findViewById(R.id.song_length);
        }

        void bindSongs(Song song) {
            songName.setText(song.getTrackName());
            songLength.setText(song.getSongLength());
            songNumber.setText(song.getTrackNumber());
        }
    }

    private class SongAdapter extends RecyclerView.Adapter<SongHolder> {

        SongAdapter(Album album) {
            mAlbum = album;
        }

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(AlbumActivity.this);
            View view = inflater.inflate(R.layout.song_holder, viewGroup, false);
            return new SongHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongHolder holder, int position) {
            Song song = mAlbum.getSongs().get(position);
            holder.bindSongs(song);
        }

        @Override
        public int getItemCount() {
            return mAlbum.getSongs().size();
        }
    }
}
