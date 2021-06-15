package rs.ac.bg.etf.running.musicplayer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistsBinding;
import rs.ac.bg.etf.running.dialogs.PlaylistCreatorDialog;
import rs.ac.bg.etf.running.dialogs.SortDialogFragment;

public class PlaylistsFragment extends Fragment {

    private FragmentPlaylistsBinding binding;
    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false);

        PlaylistAdapter adapter = new PlaylistAdapter();

        playlistViewModel.subscribeToRealtimeUpdates(adapter);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.setOnClickListener(v -> {
            PlaylistCreatorDialog dialog = new PlaylistCreatorDialog();
            dialog.show(getChildFragmentManager(), "sort-fragment");
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}