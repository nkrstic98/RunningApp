package rs.ac.bg.etf.running;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    public static final String CALORIES_BURNED_KEY = "calories-burned";

    private boolean caloriesBurnedValid = false;
    private final MutableLiveData<Integer> caloriesBurned = new MutableLiveData<>();

    public void initByInstanceStateBundle(Bundle bundle) {
        if (bundle != null) {
            if (!caloriesBurnedValid) {
                if (bundle.containsKey(CALORIES_BURNED_KEY)) {
                    caloriesBurnedValid = true;
                    caloriesBurned.setValue(bundle.getInt(CALORIES_BURNED_KEY));
                }
            }
        }
    }

    public LiveData<Integer> getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int value) {
        caloriesBurnedValid = true;
        caloriesBurned.setValue(value);
    }
}
