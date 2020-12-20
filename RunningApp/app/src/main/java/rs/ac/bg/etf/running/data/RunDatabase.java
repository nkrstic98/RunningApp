package rs.ac.bg.etf.running.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@TypeConverters(value = {DateConverter.class})
@Database(entities = {Workout.class}, version = 1, exportSchema = false)
public abstract class RunDatabase extends RoomDatabase {
    public abstract  WorkoutDao workoutDao();

    private static final String DATABASE_NAME = "run-app.db";
    private static RunDatabase instance = null;

    public static RunDatabase getInstance(Context context) {
        if(instance == null) {
            synchronized (RunDatabase.class) {
                if(instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RunDatabase.class,
                            DATABASE_NAME)
                            .build();
                }
            }

        }

        return instance;
    }
}
