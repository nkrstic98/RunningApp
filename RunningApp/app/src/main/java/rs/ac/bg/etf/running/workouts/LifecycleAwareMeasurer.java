package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.List;

import javax.inject.Inject;

import rs.ac.bg.etf.running.MainActivity;

public class LifecycleAwareMeasurer implements DefaultLifecycleObserver {

    private SensorManager sensorManager = null;

    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(MainActivity.LOG_TAG, "temp_sensor:" + event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Inject
    public LifecycleAwareMeasurer() {

    }

    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if(sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }
}
