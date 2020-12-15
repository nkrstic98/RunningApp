package rs.ac.bg.etf.running;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private final MutableLiveData<Integer> caloriesBurned = new MutableLiveData<>();

    public LiveData<Integer> getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int value) {
        caloriesBurned.setValue(value);
    }
}
