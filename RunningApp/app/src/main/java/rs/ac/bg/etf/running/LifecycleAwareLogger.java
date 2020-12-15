package rs.ac.bg.etf.running;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LifecycleAwareLogger implements LifecycleObserver {

    private final String logTag;
    private final String lifecycleOwner;

    public LifecycleAwareLogger(String logTag, String lifecycleOwner) {
        this.logTag = logTag;
        this.lifecycleOwner = lifecycleOwner;
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    public void myOnCreate() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnCreate()");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    public void myOnStart() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnStart()");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void myOnResume() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnResume()");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void myOnPause() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnPause()");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    public void myOnStop() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnStop()");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void myOnDestroy() {
        Log.d(logTag, "called " + lifecycleOwner +".myOnDestroy()");
    }

}
