package rs.ac.bg.etf.running.workouts;

import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.data.Location;
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

    MutableLiveData<Double> min_distance = new MutableLiveData<>(0.0);
    MutableLiveData<Double> max_distance = new MutableLiveData<>(100.0);
    MutableLiveData<Double> min_duration = new MutableLiveData<>(0.0);
    MutableLiveData<Double> max_duration = new MutableLiveData<>(360.0);

    MutableLiveData<String> sort_criteria = new MutableLiveData<>("date");

    MutableLiveData<Boolean> sortApplied = new MutableLiveData<>(false);
    MutableLiveData<Boolean> distanceFilterApplied = new MutableLiveData<>(false);
    MutableLiveData<Boolean> durationFilterApplied = new MutableLiveData<>(false);

    @ViewModelInject
    public WorkoutViewModel(@Assisted SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;

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

    public Workout getWorokout(int index) {
        return this.workouts.get(index);
    }

    public List<Location> getPath(int index) {
        return this.workouts.get(index).getCoordinates();
    }

    public void sortWorkouts(String cond) {
        sort_criteria.setValue(cond);

        sortApplied.setValue(true);

        workoutCollection
                .orderBy(sort_criteria.getValue(), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(command -> {
                    if(command.isSuccessful()) {

                        workouts = new ArrayList<>();
                        for (DocumentSnapshot document : command.getResult().getDocuments()) {
                            Workout workout = document.toObject(Workout.class);
                            if(workout.getDuration() >= min_duration.getValue() && workout.getDuration() <= max_duration.getValue()) {
                                if(workout.getDistance() >= min_distance.getValue() && workout.getDistance() <= max_distance.getValue()) {
                                    workouts.add(workout);
                                }
                            }
                        }

                        workoutAdapter.setWorkoutList(workouts);
                    }
                    else {
                        Log.e("err", command.getException().getMessage());
                    }
                });
    }

    public void filterWorkouts(double min_distance, double max_distance, double min_duration, double max_duration) {
        this.min_distance.setValue(min_distance);
        this.max_distance.setValue(max_distance);
        this.min_duration.setValue(min_duration);
        this.max_duration.setValue(max_duration);

        if(min_distance != 0.0 || max_distance != 100.0) {
            distanceFilterApplied.setValue(true);
        }

        if(min_duration != 0.0 || max_duration != 360.0) {
            durationFilterApplied.setValue(true);
        }

        workoutCollection
                .orderBy(sort_criteria.getValue(), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(command -> {
                    if(command.isSuccessful()) {
                        workouts = new ArrayList<>();

                        for (DocumentSnapshot document : command.getResult().getDocuments()) {
                            Workout workout = document.toObject(Workout.class);
                            if(workout.getDuration() >= min_duration && workout.getDuration() <= max_duration) {
                                if(workout.getDistance() >= min_distance && workout.getDistance() <= max_distance) {
                                    workouts.add(workout);
                                }
                            }
                        }

                        workoutAdapter.setWorkoutList(workouts);
                    }
                    else {
                        Log.e("err", command.getException().getMessage());
                    }
                });
    }

    public String getSortCriteria() {
        return "Sorted by: " + this.sort_criteria.getValue();
    }

    public String getDurationFilter() {
        StringBuilder sb = new StringBuilder("Duration: ");

        if(this.min_duration.getValue() > 0.0) {
            sb.append("from ");
            sb.append(this.min_duration.getValue().intValue());
        }

        if(this.max_duration.getValue() < 360.0) {
            sb.append(" to ");
            sb.append(this.max_duration.getValue().intValue());
        }

        sb.append(" min");

        return sb.toString();
    }

    public String getDistanceFilter() {
        StringBuilder sb = new StringBuilder("Distance: ");

        if(this.min_distance.getValue() > 0.0) {
            sb.append("from ");
            sb.append(this.min_distance.getValue().intValue());
        }

        if(this.max_distance.getValue() < 100.0) {
            sb.append(" to ");
            sb.append(this.max_distance.getValue().intValue());
        }

        sb.append(" km");

        return sb.toString();
    }

    public boolean getSortApplied() {
        return sortApplied.getValue();
    }

    public void removeSort() {
        this.sortApplied.setValue(false);
        this.sortWorkouts("date");
    }

    public boolean getDistanceFilterApplied() {
        return distanceFilterApplied.getValue();
    }

    public void resetDistanceFilter() {
        this.distanceFilterApplied.setValue(false);
        this.filterWorkouts(0.0, 100.0, this.min_duration.getValue(), this.max_duration.getValue());
    }

    public boolean getDurationFilterApplied() {
        return durationFilterApplied.getValue();
    }

    public void resetDurationFilter() {
        this.durationFilterApplied.setValue(false);
        this.filterWorkouts(this.min_distance.getValue(), this.max_distance.getValue(), 0.0, 360.0);
    }

    //-------------------------------------------------------------------------

    public double getMin_distance() {
        return min_distance.getValue();
    }

    public double getMax_distance() {
        return max_distance.getValue();
    }

    public double getMin_duration() {
        return min_duration.getValue();
    }

    public double getMax_duration() {
        return max_duration.getValue();
    }
}
