package com.example.itunestest.Album;

import android.content.Intent;
import android.net.Uri;
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

import com.example.itunestest.Model.Album;
import com.example.itunestest.Model.Song;
import com.example.itunestest.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AlbumView extends AppCompatActivity implements IAlbumView{

    public static final String ALBUM_ID = "album_id";
    private static final String TAG = "tag";

    private IAlbumPresenter mPresenter;

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
        int albumId = getIntent().getIntExtra(ALBUM_ID, 0);
        albumName = findViewById(R.id.album_name);
        artistName = findViewById(R.id.artist_name);
        albumDescription = findViewById(R.id.album_description);
        mToolbar = findViewById(R.id.album_toolbar);
        mAlbumInfo = findViewById(R.id.information_layout);
        mAlbumInfo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPresenter.getAlbumUrl()));
            startActivity(intent);
        });
        mAlbumCover = findViewById(R.id.new_cover_view);
        mAlbumRecyclerView = findViewById(R.id.album_songs_recycler_view);
        mAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(AlbumView.this));


        if (mPresenter == null) {
            mPresenter = new AlbumPresenter(this);
        }
        mPresenter.getAlbum(albumId);
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

        private Album mAlbum;

        SongAdapter(Album album) {
            mAlbum = album;
        }

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(AlbumView.this);
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


    @Override
    public void showAlbum(Album album) {
        mToolbar.setTitle(album.getCollectionName());
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        String cover = album.getArtworkUrl60()
                .replace("60x60", "600x600");
        Picasso.get().load(cover).into(mAlbumCover);

        updateAlbumDescription(album);

        mAlbumRecyclerView.setAdapter(new SongAdapter(album));
    }

    @Override
    public void showError() {
        Log.i(TAG, "showError: ALBUM ERROR");
    }

    private void updateAlbumDescription(Album mAlbum) {
        albumName.setText(mAlbum.getCollectionName());
        artistName.setText(mAlbum.getArtistName());

        /*
            в зависимости от количества треков подбираем окончание
            "track(-s)" или "трек(-а/-ов)".
         */
        int i = mAlbum.getTrackCount();
        String trackCount = getResources().getQuantityString(R.plurals.plurals_1, i, i);

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
                trackCount + "\n" +
                mAlbum.getCollectionPrice() + " " + mAlbum.getCurrency() + "\n" +
                getResources().getString(R.string.released_date) + " " +
                dateStr + "\n\n" + mAlbum.getCopyright();
        albumDescription.setText(description);
    }

}
