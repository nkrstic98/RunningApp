package rs.ac.bg.etf.running.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;
import rs.ac.bg.etf.running.firebase.FirebaseFirestoreInstance;

public class PlaylistViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;

    private List<Playlist> playlists;
    private List<String> playlistIds = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference playlistCollection;

    private PlaylistAdapter playlistAdapter;

    @ViewModelInject
    public PlaylistViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;

        firebaseAuth = FirebaseAuthInstance.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestoreInstance.getInstance();
        playlistCollection = firebaseFirestore
                .collection("users")
                .document(firebaseUser.getEmail())
                .collection("playlists");

        playlists = new ArrayList<>();
    }

    public void insertPlaylist(Playlist playlist) {
        playlistCollection
                .add(playlist)
                .addOnSuccessListener(documentReference -> {
                    Log.d("add-playlist", "Playlist added");
                })
                .addOnFailureListener(e -> {
                    Log.e("add-playlist", "Insertion failed");
                });
    }

    public void subscribeToRealtimeUpdates(PlaylistAdapter playlistAdapter) {
        this.playlistAdapter = playlistAdapter;

        playlistCollection
                .orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        Log.e("error", error.getMessage());
                        return;
                    }

                    if(value != null) {
                        playlists = new ArrayList<>();
                        for(DocumentSnapshot document : value) {
                            playlistIds.add(document.getId());
                            Playlist playlist = document.toObject(Playlist.class);
                            playlists.add(playlist);
                        }

                        this.playlistAdapter.setPlaylists(playlists);
                    }
                });
    }

    public void updatePlaylist(MainActivity mainActivity, int playlistIndex, List<Audio> audios) {
        Playlist playlist = playlists.get(playlistIndex);
        List<Audio> audioList = playlist.getAudioList();
        for(int i = 0; i < audios.size(); i++) {
            audioList.add(audios.get(i));
        }
        playlist.setAudioList(audioList);

        playlistCollection
                .document(playlistIds.get(playlistIndex))
                .update("audioList", playlist.getAudioList());
    }

    public void deleteAudioFromPlaylist(int playlistIndex, List<Audio> audios) {
        playlistCollection
                .document(playlistIds.get(playlistIndex))
                .update("audioList", audios);
    }

    public void deletePlaylist(int index) {
        playlistCollection
                .document(playlistIds.get(index))
                .delete();
    }

    public List<Audio> getAudioList(int index) {
        return playlists.get(index).getAudioList();
    }

    public boolean playlistExists(String name) {
        for (Playlist p :
                playlists) {
            if(p.getTitle().equals(name)) return true;
        }

        return false;
    }

    public boolean songAdded(int playlistIndex, String title) {
        Playlist p = playlists.get(playlistIndex);
        for (Audio a :
                p.getAudioList()) {
            if(a.getTitle().equals(title)) return true;
        }

        return false;
    }
}
