package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if(isPermissionGranted) {
                            startWorkout(new Date().getTime());
                        }
                    }
            );

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

        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()));
        }

        binding.start.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            else {
                startWorkout(new Date().getTime());
            }
        });
        binding.finish.setOnClickListener(v -> finishWorkout());
        binding.cancel.setOnClickListener(v -> cancelWorkout());


        binding.workoutDuration.setVisibility(View.INVISIBLE);
        binding.startTimeCounter.setVisibility(View.VISIBLE);

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
        binding.workoutDuration.setVisibility(View.VISIBLE);
        binding.startTimeCounter.setVisibility(View.INVISIBLE);

        timer = new Timer();

        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
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
        intent.setAction(WorkoutService.INTENT_ACTION_START);
        mainActivity.startService(intent);
    }

    private void finishWorkout() {
        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);
        workoutViewModel.insertWorkout(new Workout(
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
        timer.cancel();
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);
        sharedPreferences.edit().remove(START_TIMESTAMP_KEY).commit();
        //navController.navigateUp();

        binding.start.setEnabled(true);
        binding.finish.setEnabled(false);
        binding.cancel.setEnabled(false);

        binding.workoutDuration.setVisibility(View.INVISIBLE);
        binding.startTimeCounter.setVisibility(View.VISIBLE);
    }
}