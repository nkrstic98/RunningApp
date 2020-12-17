package rs.ac.bg.etf.running.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Workout.class}, version = 1, exportSchema = false)
public abstract class RunDatabase extends RoomDatabase {
    public abstract  WorkoutDao workoutDao();
}
