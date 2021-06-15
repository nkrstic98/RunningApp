package rs.ac.bg.etf.running.workouts;

import android.util.Log;
import android.view.View;

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
    private final SavedStateHandle savedStateHandle;

    private List<Workout> workouts;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference workoutCollection;

    private WorkoutAdapter workoutAdapter;

    private static final String SORT_KEY = "workout-sort-key";
    private String sortCondition;

    @ViewModelInject
    public WorkoutViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;

        sortCondition = savedStateHandle.get(SORT_KEY);
        if(sortCondition == null) {
            sortCondition = "date";
        }

        firebaseAuth = FirebaseAuthInstance.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestoreInstance.getInstance();
        workoutCollection = firebaseFirestore
                .collection("users")
                .document(firebaseUser.getEmail())
                .collection("workouts");

        workouts = new ArrayList<>();
    }

    public void insertWorkout(Workout workout) {
        workoutCollection
                .add(workout)
                .addOnSuccessListener(documentReference -> {
                    Log.d("add-workout", "Workout added");
                })
                .addOnFailureListener(e -> {
                    Log.e("add-workout", "Insertion failed");
                });
    }

    public void setSortCondition(String condition) {
        savedStateHandle.set(SORT_KEY, condition);
    }

    public void subscribeToRealtimeUpdates(WorkoutAdapter workoutAdapter) {
        this.workoutAdapter = workoutAdapter;

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

                        this.workoutAdapter.setWorkoutList(workouts);
                    }
                });
    }

    public void sortWorkouts(String cond) {
        workoutCollection
                .orderBy(cond, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(command -> {
                    if(command.isSuccessful()) {
                        workouts = new ArrayList<>();
                        for (DocumentSnapshot document : command.getResult().getDocuments()) {
                            Workout workout = document.toObject(Workout.class);
                            workouts.add(workout);
                        }

                        workoutAdapter.setWorkoutList(workouts);
                    }
                    else {
                        Log.e("err", command.getException().getMessage());
                    }
                });
    }
}
