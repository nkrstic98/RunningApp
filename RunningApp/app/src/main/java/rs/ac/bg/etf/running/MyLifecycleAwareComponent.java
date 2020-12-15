package rs.ac.bg.etf.running;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MyLifecycleAwareComponent implements LifecycleObserver {

    private static final String LOG_TAG = "activity-lifecycle";

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    public void myOnCreate() {
        Log.d(LOG_TAG, "myOnCreate() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    public void myOnStart() {
        Log.d(LOG_TAG, "myOnStart() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void myOnResume() {
        Log.d(LOG_TAG, "myOnResume() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void myOnPause() {
        Log.d(LOG_TAG, "myOnPause() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    public void myOnStop() {
        Log.d(LOG_TAG, "myOnStop() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void myOnDestroy() {
        Log.d(LOG_TAG, "myOnDestroy() called");
    }

}
