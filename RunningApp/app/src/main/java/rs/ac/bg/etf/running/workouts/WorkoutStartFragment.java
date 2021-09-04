package rs.ac.bg.etf.running.workouts;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStartBinding;
import rs.ac.bg.etf.running.musicplayer.CustomTouchListener;
import rs.ac.bg.etf.running.musicplayer.MediaPlayerService;
import rs.ac.bg.etf.running.musicplayer.OnItemClickListener;
import rs.ac.bg.etf.running.musicplayer.PlaylistAdapter;
import rs.ac.bg.etf.running.musicplayer.PlaylistViewModel;
import rs.ac.bg.etf.running.musicplayer.PlaylistsFragmentDirections;
import rs.ac.bg.etf.running.musicplayer.StorageUtil;

import static rs.ac.bg.etf.running.MainActivity.LOG_TAG;

@RequiresApi(api = Build.VERSION_CODES.P)
@AndroidEntryPoint
public class WorkoutStartFragment extends Fragment {
    public static final String Broadcast_PLAY_NEW_AUDIO = "rs.ac.bg.etf.running.PlayNewAudio";

    public static final String SHARED_PREFERENCES_NAME = "workout-shared-preferences";
    public static final String START_TIMESTAMP_KEY = "start-timestamp-key";
    public static final String RUNNING_PATH = "running-path";
    public static final String STEP_NUMBER = "step-number";
    public static final String SERVICE_BOUND = "service-bound";

    private FragmentWorkoutStartBinding binding;
    private WorkoutViewModel workoutViewModel;
    private PlaylistViewModel playlistViewModel;
    private MainActivity mainActivity;

    private Timer timer;
    private Timer stepTimer;
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if(isPermissionGranted) {
                            setRequestPermissionLauncher();
                        }
                    }
            );

    private MediaPlayerService player;
    boolean serviceBound = false;

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            sharedPreferences
                    .edit()
                    .putBoolean(WorkoutStartFragment.SERVICE_BOUND, serviceBound)
                    .commit();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            sharedPreferences
                    .edit()
                    .putBoolean(WorkoutStartFragment.SERVICE_BOUND, serviceBound)
                    .commit();
        }
    };

    public WorkoutStartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        playlistViewModel = new ViewModelProvider(mainActivity).get(PlaylistViewModel.class);

        sharedPreferences = mainActivity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        serviceBound = sharedPreferences.getBoolean(SERVICE_BOUND, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkoutStartBinding.inflate(inflater, container, false);

        PlaylistAdapter adapter = new PlaylistAdapter(mainActivity, playlistIndex -> {
            playAudios(playlistIndex);
        });
        adapter.setCanPlay(true);

        playlistViewModel.subscribeToRealtimeUpdates(adapter);
        binding.recyclerViewPlaylists.setAdapter(adapter);
        binding.recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(mainActivity));
        binding.recyclerViewPlaylists.addOnItemTouchListener(new CustomTouchListener(mainActivity, new OnItemClickListener() {
            @Override
            public void onClick(View view, int index) {
                playAudios(index);
            }
        }));

        binding.workoutDuration.setVisibility(View.INVISIBLE);
        binding.startTimeCounter.setVisibility(View.VISIBLE);

        if (sharedPreferences.contains(START_TIMESTAMP_KEY)) {
            startWorkout(sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime()));
        }

        binding.start.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            else {
                setRequestPermissionLauncher();
            }
        });

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
    public void onDestroyView() {
        super.onDestroyView();
        if(timer != null) {
            timer.cancel();
        }

        if(stepTimer != null) {
            stepTimer.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setRequestPermissionLauncher() {
        if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        else {
            startWorkout(new Date().getTime());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startWorkout(long startTimestamp) {
        LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isLocationEnabled()) {
            Toast.makeText(mainActivity, "Turn on Location services!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.workoutDuration.setVisibility(View.VISIBLE);
        binding.startTimeCounter.setVisibility(View.INVISIBLE);

        timer = new Timer();

        binding.start.setEnabled(false);
        binding.finish.setEnabled(true);
        binding.cancel.setEnabled(true);

        if (!sharedPreferences.contains(SERVICE_BOUND)) {
            playAudios(((int) Math.random()) % playlistViewModel.size());
        }

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

        stepTimer = new Timer();
        Handler stepHandler = new Handler(Looper.getMainLooper());
        stepTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int steps = sharedPreferences.getInt(WorkoutStartFragment.STEP_NUMBER, 0);
                stepHandler.post(() -> binding.stepTimeCounter.setText(steps + " steps"));
            }
        }, 0, 1000);

        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        intent.setAction(WorkoutService.INTENT_ACTION_START);
        mainActivity.startService(intent);
    }

    private void finishWorkout() {
        timer.cancel();
        stepTimer.cancel();
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);

        long startTimestamp = sharedPreferences.getLong(START_TIMESTAMP_KEY, new Date().getTime());
        long elapsed = new Date().getTime() - startTimestamp;
        double minutes = elapsed / (1000.0 * 60);

        Timer timer2 = new Timer();
        Handler handler = new Handler(Looper.getMainLooper());

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    int steps = sharedPreferences.getInt(STEP_NUMBER, 0);
                    String coordinates = sharedPreferences.getString(RUNNING_PATH, "");
                    List<Location> locationList = null;
                    if(!coordinates.equals("")) {
                        Gson gson = new Gson();
                        locationList = gson.fromJson(coordinates, List.class);
                    }

                    workoutViewModel.insertWorkout(new Workout(
                            new Date(),
                            getText(R.string.workout_label).toString(),
                            0.2 * minutes,
                            minutes,
                            steps,
                            locationList
                    ));
                });

                timer2.cancel();
            }
        }, 1000, 1000);

        stopWorkout();
    }

    private void cancelWorkout() {
        timer.cancel();
        stepTimer.cancel();
        Intent intent = new Intent();
        intent.setClass(mainActivity, WorkoutService.class);
        mainActivity.stopService(intent);

        stopWorkout();
    }

    private void stopWorkout() {
        sharedPreferences
                .edit()
                .remove(START_TIMESTAMP_KEY)
                .remove(STEP_NUMBER)
                .remove(SERVICE_BOUND)
                .commit();
        //navController.navigateUp();

        binding.start.setEnabled(true);
        binding.finish.setEnabled(false);
        binding.cancel.setEnabled(false);

        binding.stepTimeCounter.setText("0 steps");

        binding.workoutDuration.setVisibility(View.INVISIBLE);
        binding.startTimeCounter.setVisibility(View.VISIBLE);

        if (serviceBound && player != null && serviceConnection != null) {
            mainActivity.unbindService(serviceConnection);
            //service is active
            player.stopSelf();
            serviceBound = false;
        }
    }

    private void playAudios(int index) {
        StorageUtil storageUtil = new StorageUtil(mainActivity.getApplicationContext());
        if(!serviceBound) {
            storageUtil.storeAudio(playlistViewModel.getAudioList(index));
            storageUtil.storeAudioIndex(0);

            Intent playerIntent = new Intent(mainActivity, MediaPlayerService.class);
            mainActivity.startService(playerIntent);
            mainActivity.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            storageUtil.storeAudioIndex(index);

            //Service is active
            //Send broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            mainActivity.sendBroadcast(broadcastIntent);
        }
    }
}