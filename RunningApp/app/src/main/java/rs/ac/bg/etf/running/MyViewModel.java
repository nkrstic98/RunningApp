package rs.ac.bg.etf.running;

import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private int caloriesBurned;

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int value) {
        caloriesBurned = value;
    }
}
