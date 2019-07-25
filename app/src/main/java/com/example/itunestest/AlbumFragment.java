package com.example.itunestest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AlbumFragment extends Fragment {

    public static final String ALBUM_ID = "album_id";
    private static final String TAG = "tag";
    private RecyclerView mAlbumRecyclerView;
    private Album mAlbum;
    private String albumId;
    private ImageView mAlbumCover;
    private Toolbar mToolbar;

    public static AlbumFragment newInstance(String albumId) {
        Bundle args = new Bundle();
        args.putString(ALBUM_ID, albumId);
        AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        albumId = getArguments().getString(ALBUM_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        mAlbumCover = getActivity().findViewById(R.id.new_cover_view);
        mToolbar = getActivity().findViewById(R.id.album_toolbar);
        mAlbumRecyclerView = view.findViewById(R.id.album_songs_recycler_view);
        mAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new FetchSongsTask().execute();
        return view;
    }


    private class FetchSongsTask extends AsyncTask<Void, Void, Album> {

        @Override
        protected Album doInBackground(Void... voids) {
            return new ITunesFetchr().fetchAlbum(albumId);
        }

        @Override
        protected void onPostExecute(Album album) {
            mAlbum = album;
            setupSongAdapter();
        }
    }


    private class SongHolder extends RecyclerView.ViewHolder {

        private TextView songName;

        public SongHolder(View albumView) {
            super(albumView);
            songName = albumView.findViewById(R.id.song_name);
        }

        public void bindSongs(Song song) {
            songName.setText(song.getTrackName());
        }
    }

    private class SongAdapter extends RecyclerView.Adapter<SongHolder> {

        public SongAdapter(Album album) {
            mAlbum = album;
        }

        @NonNull
        @Override
        public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
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

    private void setupSongAdapter() {
        if (isAdded()) {
            mToolbar.setTitle(mAlbum.getCollectionName());
            String cover = mAlbum.getArtworkUrl60()
                    .replace("60x60", "600x600");
            Picasso.get().load(cover).into(mAlbumCover);
            mAlbumRecyclerView.setAdapter(new SongAdapter(mAlbum));
        }
    }
}
