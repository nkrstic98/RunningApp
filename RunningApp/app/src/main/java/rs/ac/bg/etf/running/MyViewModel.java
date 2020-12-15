package rs.ac.bg.etf.running;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    public static final String CALORIES_BURNED_KEY = "calories-burned";
    public static final String CALORIES_NEEDED_KEY = "calories-needed";

    private boolean dataValid = false;
    private final MutableLiveData<Integer> caloriesBurned = new MutableLiveData<>(-1);
    private final MutableLiveData<Integer> caloriesNeeded = new MutableLiveData<>(-1);

    public void initByInstanceStateBundle(Bundle bundle) {
        if (bundle != null) {
            if (!dataValid) {
                if (bundle.containsKey(CALORIES_BURNED_KEY)) {
                    dataValid = true;
                    caloriesBurned.setValue(bundle.getInt(CALORIES_BURNED_KEY));
                }
                if (bundle.containsKey(CALORIES_NEEDED_KEY)) {
                    dataValid = true;
                    caloriesBurned.setValue(bundle.getInt(CALORIES_NEEDED_KEY));
                }
            }
        }
    }

    public LiveData<Integer> getCaloriesBurned() {
        return caloriesBurned;
    }

    public LiveData<Integer> getCaloriesNeeded() {
        return caloriesNeeded;
    }

    public void updateValues(
            double weight,
            double height,
            int age,
            boolean isMale,
            double duration,
            double met) {
        dataValid = true;

        double caloriesNeededValue;
        if (isMale) {
            caloriesNeededValue = 66 + 13.7 * weight + 5 * height - 6.8 * age;
        } else {
            caloriesNeededValue = 655.1 + 9.6 * weight + 1.9 * height - 4.7 * age;
        }
        caloriesNeeded.setValue((int) caloriesNeededValue);

        double caloriesBurnedValue = duration * met * 3.5 * weight / 200;
        caloriesBurned.setValue((int) caloriesBurnedValue);
    }
}
