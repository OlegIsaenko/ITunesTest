package com.example.itunestest.AlbumList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itunestest.Album.AlbumView;
import com.example.itunestest.Model.Album;
import com.example.itunestest.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;


public class AlbumListView extends AppCompatActivity implements IAlbumListView {

    public static final String TAG = "inList";

    private IAlbumListPresenter presenter;

    private RecyclerView mRecyclerView;

    private boolean isToast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = findViewById(R.id.list_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        if (presenter == null) {
            presenter = new AlbumListPresenter(this);
        }
        mRecyclerView = findViewById(R.id.itunes_test_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchView menuSearchView = (SearchView) searchMenuItem.getActionView();
        menuSearchView.setIconifiedByDefault(false);
        menuSearchView.setMaxWidth(Integer.MAX_VALUE);

        Observable.create((ObservableOnSubscribe<String>) subscriber ->
                menuSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        subscriber.onNext(s);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        subscriber.onNext(s);
                        return false;
                    }
                })
        )
                .map(text -> text.toLowerCase().trim())
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .filter(text -> !text.isEmpty())
                .subscribe(text -> presenter.getAlbumsFromModel(text)
                 );

        return true;
    }

    @Override
    public void showAlbums(List<Album> albums) {
        mRecyclerView.setAdapter(new AlbumAdapter(albums));
    }

    @Override
    public void showError() {
        if(!isToast) {
            Toast toast = Toast.makeText(this,
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


    private class AlbumHolder extends RecyclerView.ViewHolder {
        private ImageView artworkUrl60;
        private TextView collectionName;
        private TextView artistName;


        AlbumHolder(View albumView) {
            super(albumView);
            artworkUrl60 = albumView.findViewById(R.id.list_albums_cover);
            collectionName = albumView.findViewById(R.id.album_name);
            artistName = albumView.findViewById(R.id.artist_name);
        }

        void bindAlbumsItem(Album album) {
            collectionName.setText(album.getCollectionName());
            artistName.setText(album.getArtistName());
            String cover = album.getArtworkUrl60()
                    .replace("60x60", "250x250");
            Picasso.get().load(cover).into(artworkUrl60);
        }
    }

    private class AlbumAdapter extends RecyclerView.Adapter<AlbumHolder> {

        private List<Album> mAlbumItems;

        AlbumAdapter(List<Album> albumItems) {
            mAlbumItems = albumItems;
        }

        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(AlbumListView.this);
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
            holder.itemView.setOnClickListener(v -> {
                if (presenter.isOnline(AlbumListView.this)) {
                    int album_id = mAlbumItems.get(adapterPosition).getCollectionId();
                    Intent intent = new Intent(AlbumListView.this, AlbumView.class);
                    intent.putExtra(AlbumView.ALBUM_ID, album_id);
                    startActivity(intent);
                }
                else  {
                    showError();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAlbumItems.size();
        }
    }
}
