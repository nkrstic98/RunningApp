package rs.ac.bg.etf.running.workouts;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import rs.ac.bg.etf.running.MainActivity;

public class WorkoutService extends Service {

    @Override
    public void onCreate() {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onCreate()");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(MainActivity.LOG_TAG, "WorkoutService.onBind()");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        Log.d(MainActivity.LOG_TAG, "WorkoutService.onDestroy()");
        super.onDestroy();
    }
}