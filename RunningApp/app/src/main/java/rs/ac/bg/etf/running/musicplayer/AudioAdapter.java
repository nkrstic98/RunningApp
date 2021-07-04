package rs.ac.bg.etf.running.musicplayer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import rs.ac.bg.etf.running.databinding.ViewHolderAudioBinding;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    List<Audio> audioList;

    public AudioAdapter() {

    }

    public void setAudioList(List<Audio> audioList) {
        this.audioList = audioList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewHolderAudioBinding binding = ViewHolderAudioBinding.inflate(inflater, parent, false);
        return new AudioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.binding.title.setText(audioList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder {
        private ViewHolderAudioBinding binding;

        public AudioViewHolder(ViewHolderAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
