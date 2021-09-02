package rs.ac.bg.etf.running.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.databinding.FragmentPlaylistsBinding;
import rs.ac.bg.etf.running.dialogs.PlaylistCreatorDialog;

import static rs.ac.bg.etf.running.dialogs.PlaylistCreatorDialog.REQUEST_CODE;

public class PlaylistsFragment extends Fragment {

    private FragmentPlaylistsBinding binding;
    private MainActivity mainActivity;
    private PlaylistViewModel playlistViewModel;
    private NavController navController;

    Playlist playlist = null;
    List<Audio> audioList;

    public static String playlist_name = "";

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

        PlaylistAdapter adapter = new PlaylistAdapter(mainActivity, routeIndex -> {
            PlaylistsFragmentDirections.ActionPlaylistsToAudioFragment action = PlaylistsFragmentDirections.actionPlaylistsToAudioFragment(routeIndex);
            action.setPlaylistIndex(routeIndex);
            navController.navigate(action);
        });

        playlistViewModel.subscribeToRealtimeUpdates(adapter);

        binding.recyclerViewPlaylists.setAdapter(adapter);
        binding.recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(mainActivity));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mainActivity, adapter));
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewPlaylists);

        binding.floatingActionButton.setOnClickListener(v -> {
            playlist = new Playlist();
            permission();
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(mainActivity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Intent multipleAudioChooser = intent.createChooser(intent, "Choose a file");
            startActivityForResult(multipleAudioChooser, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if(null != data) { // checking empty selection
                audioList = new ArrayList<>();
                if(null != data.getClipData()) { // checking multiple selection or not
                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        Log.d("my-chosen-audio", uri + "");
                        loadAudio(uri);
                    }
                } else {
                    Uri uri = data.getData();
                    loadAudio(uri);
                }
                savePlaylist();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                Intent multipleAudioChooser = intent.createChooser(intent, "Choose a file");
                startActivityForResult(multipleAudioChooser, 1);
            }
            else {
                ActivityCompat.requestPermissions(mainActivity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void loadAudio(Uri uri) {
        ContentResolver contentResolver = mainActivity.getContentResolver();

//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist, duration));
            }
        }
        cursor.close();
    }

    private void savePlaylist() {
        PlaylistCreatorDialog dialog = new PlaylistCreatorDialog(playlist, playlistViewModel);
        dialog.show(getChildFragmentManager(), "playlist-fragment");

        playlist.setAudioList(audioList);
    }
}