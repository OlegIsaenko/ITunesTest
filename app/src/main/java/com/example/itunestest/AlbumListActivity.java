package com.example.itunestest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AlbumListActivity extends AppCompatActivity {

    public static final String TAG = "inList";
    public static final String ADAPTER_POSITION = "position";
    private RecyclerView mRecyclerView;
    private List<Album> mAlbumItems = new ArrayList<>();
    private String albumName;

    private MenuItem searchMenuItem;
    private SearchView mMenuSearchView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        mToolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerView = findViewById(R.id.itunes_test_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        mMenuSearchView = (SearchView) searchMenuItem.getActionView();
        mMenuSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                albumName = s;
                new FetchItemsTask().execute();
                return false;
            }
        });
        return true;
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<Album>> {

        @Override
        protected List<Album> doInBackground(Void... voids) {
            return new ITunesFetchr().fetchAlbums(albumName);
        }

        @Override
        protected void onPostExecute(List<Album> albumItems) {
            if (albumItems != null) {
                mAlbumItems = albumItems;
                Collections.sort(mAlbumItems, Album.ALPHABETICAL_ORDER);
                setupAlbumAdapter();
            }
        }
    }

    private class AlbumHolder extends RecyclerView.ViewHolder {
        private ImageView artworkUrl60;
        private TextView collectionName;
        private TextView artistName;
        private TextView albumId;

        public AlbumHolder(View albumView) {
            super(albumView);
            artworkUrl60 = albumView.findViewById(R.id.list_albums_cover);
            collectionName = albumView.findViewById(R.id.album_name);
            artistName = albumView.findViewById(R.id.artist_name);
            albumId = albumView.findViewById(R.id.album_id);
        }

        public void bindAlbumsItem(Album album) {
            collectionName.setText("Album: " + album.getCollectionName());
            artistName.setText("Artist: " + album.getArtistName() + "\n" + album.getTrackCount());
            String cover = album.getArtworkUrl60()
                    .replace("60x60", "250x250");
            Picasso.get().load(cover).into(artworkUrl60);
            albumId.setText(album.getCollectionId());
        }
    }

    private class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder> {

        private List<Album> mAlbumItems;

        public AlbumAdapter(List<Album> albumItems) {
            mAlbumItems = albumItems;
        }

        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(AlbumListActivity.this);
            View view = inflater.inflate(R.layout.album_holder, viewGroup, false);
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
            Album item = mAlbumItems.get(position);
            holder.bindAlbumsItem(item);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull AlbumHolder holder) {
            final int adapterPosition = holder.getAdapterPosition();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String album_id = mAlbumItems.get(adapterPosition).getCollectionId();
                    Intent intent = new Intent(AlbumListActivity.this, AlbumActivity.class);
                    intent.putExtra(AlbumFragment.ALBUM_ID, album_id);
                    startActivity(intent);
                }
            });
        }



        @Override
        public int getItemCount() {
            return mAlbumItems.size();
        }
    }

    private void setupAlbumAdapter() {
        mRecyclerView.setAdapter(new AlbumAdapter(mAlbumItems));
    }
}
