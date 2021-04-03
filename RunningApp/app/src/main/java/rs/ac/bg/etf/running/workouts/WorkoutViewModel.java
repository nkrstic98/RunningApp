package rs.ac.bg.etf.running.workouts;

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

import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;
import rs.ac.bg.etf.running.firebase.FirebaseFirestoreInstance;

public class WorkoutViewModel extends ViewModel {
//    private final WorkoutRepository workoutRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";

    private  boolean sorted = false;

//    private final LiveData<List<Workout>> workouts;
    private List<Workout> workouts;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference workoutCollection;

    @ViewModelInject
    public WorkoutViewModel(@Assisted SavedStateHandle savedStateHandle) {
//        this.workoutRepository = workoutRepository;
        this.savedStateHandle = savedStateHandle;

        firebaseAuth = FirebaseAuthInstance.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestoreInstance.getInstance();
        workoutCollection = firebaseFirestore
                .collection("users")
                .document(firebaseUser.getEmail())
                .collection("workouts");

        workouts = new ArrayList<>();

//        workouts = Transformations.switchMap(
//                savedStateHandle.getLiveData(SORTED_KEY, false),
//                sorted -> {
//                    if(!sorted) {
////                        return workoutRepository.getAllLiveData();
//                        return null;
//                    }
//                    else {
////                        return workoutRepository.getAllSortedLiveData();
//                        return null;
//                    }
//                }
//        );
    }

    public void invertSorted() {
        savedStateHandle.set(SORTED_KEY, sorted = !sorted);
    }

    public void insertWorkout(Workout workout) {
        workout.setUsername(firebaseUser.getEmail());

        workoutCollection
                .add(workout)
                .addOnSuccessListener(documentReference -> {
                    Log.d("add-workout", "Workout added");
                })
                .addOnFailureListener(e -> {
                    Log.e("add-workout", "Insertion failed");
                });

//        workoutRepository.insert(workout);
    }

//    public List<Workout> getWorkoutList() {
//        return workouts;
//    }

    public void subscribeToRealtimeUpdates(WorkoutAdapter workoutAdapter) {
        workoutCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        Log.e("error", error.getMessage());
                        return;
                    }

                    if(value != null) {
                        workouts = new ArrayList<>();
                        for (DocumentSnapshot document : value) {
                            Workout workout = document.toObject(Workout.class);
                            workouts.add(workout);
                        }

                        workoutAdapter.setWorkoutList(workouts);
                    }
                });
    }
}
