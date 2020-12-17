package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public class WorkoutRepository {

    private final WorkoutDao workoutDao;

    public WorkoutRepository(WorkoutDao workoutDao) {
        this.workoutDao = workoutDao;
    }

    public long insert(Workout workout) {
        return workoutDao.insert(workout);
    }

    public List<Workout> getAll() {
        return workoutDao.getAll();
    }

    public LiveData<List<Workout>> getAllLiveData() {
        return workoutDao.getAllLiveData();
    }
}
