package rs.ac.bg.etf.running.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.databinding.ViewHolderPlaylistsBinding;

public class PlaylistAdapter extends BaseAdapter {

    public interface Callback<T> {
        void invoke(T parameter);
    }

    List<Playlist> playlists = new ArrayList<>();

    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;
    private final Callback<Integer> callback;

    private Playlist deletedItem;
    private int deleteditemPosition;
    private boolean delete = false;

    public PlaylistAdapter(MainActivity mainActivity, Callback<Integer> callback) {
        this.mainActivity = mainActivity;
        this.playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
        this.callback = callback;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderPlaylistsBinding binding = ViewHolderPlaylistsBinding.inflate(inflater, parent, false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
        playlistViewHolder.binding.title.setText(playlists.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    @Override
    public void deleteItem(int position) {
        deletedItem = playlists.get(position);
        deleteditemPosition = position;
        delete = true;
        playlists.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    @Override
    public void showUndoSnackbar() {
        View view = mainActivity.findViewById(R.id.recycler_view_playlists);
        Snackbar snackbar = Snackbar.make(view, "Playlist deleted!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(delete) {
                    playlistViewModel.deletePlaylist(deleteditemPosition);
                    delete = false;
                }
            }


        });
        snackbar.show();
    }

    @Override
    public void undoDelete() {
        delete = false;
        playlists.add(deleteditemPosition, deletedItem);
        notifyItemInserted(deleteditemPosition);
    }

    public class PlaylistViewHolder extends BaseAdapter.BaseViewHolder {
        private ViewHolderPlaylistsBinding binding;

        public PlaylistViewHolder(ViewHolderPlaylistsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.cardview.setOnClickListener(v -> {
                callback.invoke(getAdapterPosition());
            });
        }
    }
}