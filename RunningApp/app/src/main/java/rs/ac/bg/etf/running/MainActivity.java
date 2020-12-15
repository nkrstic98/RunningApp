package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import rs.ac.bg.etf.running.calories.CaloriesFragment;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.routes.RouteBrowseFragment;
import rs.ac.bg.etf.running.routes.RouteFragment;
import rs.ac.bg.etf.running.routes.RouteViewModel;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "fragment-example";

    private ActivityMainBinding binding;
    private RouteViewModel routeViewModel;

    private FragmentManager fragmentManager;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        fragmentManager = getSupportFragmentManager();

        //dohvatimo NavHostFragment
        NavHostFragment navHost = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        //dohvatimo NavController
        navController = navHost.getNavController();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_routes:
                    switch (navController.getCurrentDestination().getId()) {
                        case R.id.calories:
                            navController.navigate(R.id.action_calories_pop);
                            break;

                        default:
                            //do nothing
                            break;
                    }
                    return true;

                case R.id.menu_item_calories:
                    switch (navController.getCurrentDestination().getId()) {
                        case R.id.route_browse:
                        case R.id.route_details:
                            navController.navigate(R.id.action_global_calories);
                            break;

                        default:
                            //do nothing
                            break;
                    }
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        switch(navController.getCurrentDestination().getId()) {
            case R.id.route_details:
                routeViewModel.setSelectedRoute(null);
                break;
        }
        super.onBackPressed();
    }
}