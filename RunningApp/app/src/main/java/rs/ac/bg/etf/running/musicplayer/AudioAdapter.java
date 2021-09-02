package rs.ac.bg.etf.running.musicplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.ViewHolderAudioBinding;

public class AudioAdapter extends BaseAdapter {

    List<Audio> audioList;

    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;

    MutableLiveData<Integer> playlistIndex = new MutableLiveData<>(-1);

    private Audio deletedItem;
    private int deleteditemPosition;
    private boolean delete = false;

    public AudioAdapter(MainActivity mainActivity, int playlistIndex) {
        this.mainActivity = mainActivity;
        this.playlistIndex.setValue(playlistIndex);
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
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
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
        audioViewHolder.binding.title.setText(audioList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    @Override
    public void deleteItem(int position) {
        deletedItem = audioList.get(position);
        deleteditemPosition = position;
        delete = true;
        audioList.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    @Override
    public void showUndoSnackbar() {
        View view = mainActivity.findViewById(R.id.recycler_view_audios);
        Snackbar snackbar = Snackbar.make(view, "Audio removed from playlist!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete());
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if(delete) {
                    playlistViewModel.deleteAudioFromPlaylist(playlistIndex.getValue(), audioList);
                    delete = false;
                }
            }
        });
        snackbar.show();
    }

    @Override
    public void undoDelete() {
        delete = false;
        audioList.add(deleteditemPosition, deletedItem);
        notifyItemInserted(deleteditemPosition);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class AudioViewHolder extends BaseAdapter.BaseViewHolder {
        private ViewHolderAudioBinding binding;

        public AudioViewHolder(ViewHolderAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
