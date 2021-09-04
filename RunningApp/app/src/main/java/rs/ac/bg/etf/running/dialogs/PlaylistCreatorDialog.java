package rs.ac.bg.etf.running.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.DialogCreatePlaylistBinding;
import rs.ac.bg.etf.running.data.Playlist;
import rs.ac.bg.etf.running.musicplayer.PlaylistViewModel;

public class PlaylistCreatorDialog extends DialogFragment {
    public static final int REQUEST_CODE = 1;

    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;
    private DialogCreatePlaylistBinding binding;

    private AlertDialog dialog;

    Playlist playlist = null;

    public PlaylistCreatorDialog(Playlist playlist, PlaylistViewModel playlistViewModel) {
        this.playlist = playlist;
        this.playlistViewModel = playlistViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCreatePlaylistBinding.inflate(inflater, container, false);

        return  binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = mainActivity.getLayoutInflater();

        builder
                .setTitle("Create playlist")
                .setView(inflater.inflate(R.layout.dialog_create_playlist, null))
                .setPositiveButton("Create playlist", (dialog1, which) -> {

                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                });

        dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String playlist_title = ((EditText) dialog.findViewById(R.id.title_edit_text)).getText().toString();
                if(playlist_title.equals("")) {
                    mainActivity.runOnUiThread(() -> {
                        binding.title.getEditText().requestFocus();
                        Toast.makeText(mainActivity, "Choose Playlist title", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    if(!playlistViewModel.playlistExists(playlist_title)) {
                        playlist.setTitle(playlist_title);
                        playlistViewModel.insertPlaylist(playlist);

                        dismiss();
                    }
                    else {
                        mainActivity.runOnUiThread(() -> {
                            binding.title.getEditText().requestFocus();
                            Toast.makeText(mainActivity, "Playlist with that name already exists!", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        });

        return dialog;
    }
}
