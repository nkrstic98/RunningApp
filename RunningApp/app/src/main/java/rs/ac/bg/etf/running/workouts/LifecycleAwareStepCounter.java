package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import static rs.ac.bg.etf.running.MainActivity.LOG_TAG;

public class LifecycleAwareStepCounter implements DefaultLifecycleObserver {

    private SensorManager sensorManager;
    private Context context;
    private SharedPreferences sharedPreferences;
    private MutableLiveData<Integer> steps = new MutableLiveData<>(0);

    private final SensorEventListener listener = new SensorEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onSensorChanged(SensorEvent event) {
            steps.setValue(steps.getValue() + 1);
            Log.d(LOG_TAG, "steps: " + steps.getValue());
            sharedPreferences
                    .edit()
                    .putInt(WorkoutStartFragment.STEP_NUMBER, steps.getValue())
                    .apply();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public LifecycleAwareStepCounter() {
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void start(Context context) {
        sharedPreferences = context.getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(sensor == null) {
            Toast.makeText(context, "Not possible", Toast.LENGTH_SHORT).show();
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public int stop() {
        return steps.getValue();
    }


    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if(sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
