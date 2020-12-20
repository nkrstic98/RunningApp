package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public class WorkoutRepository {

    private final ExecutorService executorService;

    private final WorkoutDao workoutDao;

    @Inject
    public WorkoutRepository(WorkoutDao workoutDao, ExecutorService executorService) {
        this.workoutDao = workoutDao;
        this.executorService = executorService;
    }

    public void insert(Workout workout) {
        executorService.submit(() -> workoutDao.insert(workout));
    }

    public List<Workout> getAll() {
        return workoutDao.getAll();
    }

    public LiveData<List<Workout>> getAllLiveData() {
        return workoutDao.getAllLiveData();
    }

    public LiveData<List<Workout>> getAllSortedLiveData() {
        return workoutDao.getAllSortedLiveData();
    }
}
