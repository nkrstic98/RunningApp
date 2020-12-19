package rs.ac.bg.etf.running.workouts;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.data.WorkoutRepository;

public class WorkoutViewModel extends ViewModel {
    private final WorkoutRepository workoutRepository;

    private final MutableLiveData<Boolean> sorted = new MutableLiveData<>(false);

    @ViewModelInject
    public WorkoutViewModel(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    public void invertSorted() {
        sorted.setValue(!sorted.getValue());
    }

    public void insertWorkout(Workout workout) {
        workoutRepository.insert(workout);
    }

    public LiveData<List<Workout>> getWorkoutList() {
        return Transformations.switchMap(sorted, sorted -> {
           if(!sorted) {
               return workoutRepository.getAllLiveData();
           }
           else {
               return workoutRepository.getAllSortedLiveData();
           }
        });
    }
}
