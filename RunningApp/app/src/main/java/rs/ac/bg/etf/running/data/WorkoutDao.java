package rs.ac.bg.etf.running.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import rs.ac.bg.etf.running.data.Workout;

@Dao
public interface WorkoutDao {

    @Insert
    long insert(Workout workout);

    @Query("SELECT * FROM Workout")
    List<Workout> getAll();

    @Query("SELECT * FROM Workout")
    LiveData<List<Workout>> getAllLiveData();
}
