package com.example.itunestest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ITunesTestFragment extends Fragment {

    public static final String TAG = "tag";
    private RecyclerView mRecyclerView;
    private List<Album> mAlbumItems = new ArrayList<>();
    private SearchView mSearchView;
    private String albumName;

    public static ITunesTestFragment newInstance() {
        return new ITunesTestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
//        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itunes_test, container, false);

        mSearchView = view.findViewById(R.id.search_test);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.i(TAG, "onQueryTextChange: " + s);
                albumName = s;
                new FetchItemsTask().execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
        mRecyclerView = view.findViewById(R.id.itunes_test_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAlbumAdapter();

        return view;
    }

    private void setupAlbumAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new AlbumAdapter(mAlbumItems));
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<Album>> {

        @Override
        protected List<Album> doInBackground(Void... voids) {
            return new ITunesFetchr().fetchAlbums(albumName);
        }

        @Override
        protected void onPostExecute(List<Album> albumItems) {
            mAlbumItems = albumItems;
            setupAlbumAdapter();
        }
    }

    private class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView artworkUrl60;
        private TextView collectionName;
        private TextView artistName;
        private TextView albumId;

        public AlbumHolder(View albumView) {
            super(albumView);
            albumView.setOnClickListener(this);
            artworkUrl60 = albumView.findViewById(R.id.list_albums_cover);
            collectionName = albumView.findViewById(R.id.album_name);
            artistName = albumView.findViewById(R.id.artist_name);
            albumId = albumView.findViewById(R.id.album_id);
        }

        public void bindAlbumsItem(Album album) {
            collectionName.setText("Album: " + album.getCollectionName());
            artistName.setText("Artist: " + album.getArtistName());
            Picasso.get().load(album.getCover())
                    .resize(250, 250)
                    .into(artworkUrl60);
            albumId.setText(album.getCollectionId());
        }

        @Override
        public void onClick(View v) {
            TextView textView = v.findViewById(R.id.album_id);
            String album_id = textView.getText().toString();
            Log.i(TAG, "onClick: ");
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra(AlbumFragment.ALBUM_ID, album_id);
            startActivity(intent);

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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.album_holder, viewGroup, false);
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {
            Album item = mAlbumItems.get(position);
            holder.bindAlbumsItem(item);
        }

        @Override
        public int getItemCount() {
            return mAlbumItems.size();
        }
    }


}
