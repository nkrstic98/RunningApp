package rs.ac.bg.etf.running.data;

import androidx.room.Dao;
import androidx.room.Insert;

import rs.ac.bg.etf.running.data.Workout;

@Dao
public interface WorkoutDao {

    @Insert
    long insert(Workout workout);
}
