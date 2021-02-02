package rs.ac.bg.etf.running.workouts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;

@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {

    private static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    private static final String START_TIMESTAMP_KEY = "start-timestamp-key";

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private Timer timer;
    private SharedPreferences sharedPreferences;

    public WorkoutStartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

        sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkoutStartBinding.inflate(inflater, container, false);

        timer = new Timer();

        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()));
        }

        binding.start.setOnClickListener(v -> startWorkout(new Date().getTime()));
        binding.finish.setOnClickListener(v -> finishWorkout());
        binding.cancel.setOnClickListener(v -> cancelWorkout());

        mainActivity.getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        stopWorkout();
                    }
                }
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
    }

    private void startWorkout(long startTimestamp) {
        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
        binding.power.setEnabled(true);
        binding.cancel.setEnabled(true);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(START_TIMESTAMP_KEY, startTimestamp);
        editor.commit();

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = new Date().getTime() - startTimestamp;

                int miliseconds = (int) ((elapsed % 1000) / 10);
                int seconds = (int) ((elapsed / 1000) % 60);
                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 60);

                StringBuilder workoutDuration = new StringBuilder();
                workoutDuration.append(String.format("%02d", hours)).append(":");
                workoutDuration.append(String.format("%02d", minutes)).append(":");
                workoutDuration.append(String.format("%02d", seconds)).append(".");
                workoutDuration.append(String.format("%02d", miliseconds));

                handler.post(() -> binding.workoutDuration.setText(workoutDuration));
            }
        }, 0, 10);

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.startService(intent);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> Toast.makeText(
                        mainActivity,
                        getResources().getStringArray(
                                R.array.workout_toast_motivation)[0],
                                Toast.LENGTH_SHORT).show()
                );
            }
        }, 0, 7000);
    }

    private void finishWorkout() {
        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        workoutViewModel.insertWorkout(new Workout(
                0,
                new Date(),
                getText(R.string.workout_label).toString(),
                0.2 * minutes,
                minutes
        ));
        stopWorkout();
    }

    private void cancelWorkout() {
        stopWorkout();
    }

    private void stopWorkout() {
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        navController.navigateUp();
    }
}