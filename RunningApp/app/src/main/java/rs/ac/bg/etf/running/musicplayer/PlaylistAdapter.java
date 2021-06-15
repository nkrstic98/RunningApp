package rs.ac.bg.etf.running.musicplayer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.databinding.ViewHolderPlaylistsBinding;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    List<Playlist> playlists = new ArrayList<>();

    public PlaylistAdapter() {

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
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.binding.title.setText(playlists.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ViewHolderPlaylistsBinding binding;

        public PlaylistViewHolder(ViewHolderPlaylistsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.deletePlaylist.setOnClickListener(v -> {

            });
        }
    }
}
