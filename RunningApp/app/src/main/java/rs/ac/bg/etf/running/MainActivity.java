package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.content.Intent;
import android.os.Bundle;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_NOTIFICATION = "rs.ac.bg.etf.running.NOTIFICATION";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState == null) {
            setupBottomNavigation();
        }

        if(getIntent().getAction().equals(INTENT_ACTION_NOTIFICATION)) {
            NavController navController = BottomNavigationUtil.changeNavHostFragment(R.id.bottom_navigation_workouts);
            if(navController != null) {
                navController.navigate(WorkoutListFragmentDirections.startWorkout());
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        int[] navResourceIds = new int[] {
                R.navigation.nav_graph_routes,
                R.navigation.nav_graph_workouts,
                R.navigation.nav_graph_calories
        };

        BottomNavigationUtil.setup(
                binding.bottomNavigation,
                getSupportFragmentManager(),
                navResourceIds,
                R.id.nav_host_container
        );
    }
}