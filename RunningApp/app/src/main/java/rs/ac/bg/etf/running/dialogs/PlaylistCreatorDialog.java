package rs.ac.bg.etf.running.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.DialogCreatePlaylistBinding;
import rs.ac.bg.etf.running.musicplayer.Playlist;
import rs.ac.bg.etf.running.musicplayer.PlaylistViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

public class PlaylistCreatorDialog extends DialogFragment {
    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;
    private DialogCreatePlaylistBinding binding;

    private AlertDialog dialog;

    public PlaylistCreatorDialog() {

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
                .setPositiveButton("Create playlist", null)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                });

        dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String playlist_title = binding.title.getEditText().getText().toString();
                if(playlist_title.equals("")) {
                    mainActivity.runOnUiThread(() -> {
                        binding.title.getEditText().requestFocus();
                        Toast.makeText(mainActivity, "Choose Playlist title", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    //izabrati datoteke sa uredjaja i napuniti playlistu i sacuvati je
                }
            });
        });

        return dialog;
    }
}
