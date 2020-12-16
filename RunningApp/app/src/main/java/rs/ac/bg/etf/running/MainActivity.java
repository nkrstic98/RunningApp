package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState == null) {
            setupBottomNavigation();
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