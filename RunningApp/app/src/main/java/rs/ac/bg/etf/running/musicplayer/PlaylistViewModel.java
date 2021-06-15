package rs.ac.bg.etf.running.musicplayer;

import android.util.Log;

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

import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;
import rs.ac.bg.etf.running.firebase.FirebaseFirestoreInstance;

public class PlaylistViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;

    private List<Playlist> playlists;

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
                            Playlist playlist = document.toObject(Playlist.class);
                            playlists.add(playlist);
                        }

                        this.playlistAdapter.setPlaylists(playlists);
                    }
                });
    }
}
