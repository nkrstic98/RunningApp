package rs.ac.bg.etf.running.workouts;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.data.WorkoutRepository;

public class WorkoutViewModel extends ViewModel {
    private final WorkoutRepository workoutRepository;
    private final SavedStateHandle savedStateHandle;

    private static final String SORTED_KEY = "sorted-key";

    private  boolean sorted = false;

    private final LiveData<List<Workout>> workouts;

    @ViewModelInject
    public WorkoutViewModel(WorkoutRepository workoutRepository, @Assisted SavedStateHandle savedStateHandle) {
        this.workoutRepository = workoutRepository;
        this.savedStateHandle = savedStateHandle;

        workouts = Transformations.switchMap(
                savedStateHandle.getLiveData(SORTED_KEY, false),
                sorted -> {
                    if(!sorted) {
                        return workoutRepository.getAllLiveData();
                    }
                    else {
                        return workoutRepository.getAllSortedLiveData();
                    }
                }
        );
    }

    public void invertSorted() {
        savedStateHandle.set(SORTED_KEY, sorted = !sorted);
    }

    public void insertWorkout(Workout workout) {
        workoutRepository.insert(workout);
    }

    public LiveData<List<Workout>> getWorkoutList() {
        return workouts;
    }
}
