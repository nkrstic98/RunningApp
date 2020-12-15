package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

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

    private static final String CALORIES_TAG = "fragment-calories-tag";
    private CaloriesFragment caloriesFragment;

    private static final String ROUTE_TAG = "fragment-route-tag";
    private RouteFragment routeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        routeViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().size() == 0) {
            caloriesFragment = new CaloriesFragment();
            routeFragment = new RouteFragment();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_layout, caloriesFragment, CALORIES_TAG)
                    .add(R.id.frame_layout, routeFragment, ROUTE_TAG)
                    .hide(caloriesFragment)
                    .show(routeFragment)
                    .commit();
        } else {
            caloriesFragment = (CaloriesFragment) fragmentManager
                    .findFragmentByTag(CALORIES_TAG);
            routeFragment = (RouteFragment) fragmentManager
                    .findFragmentByTag(ROUTE_TAG);
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_routes:
//                    Toast.makeText(this, "routes", Toast.LENGTH_SHORT).show();
                    fragmentManager
                            .beginTransaction()
//                            .replace(R.id.frame_layout, new RouteBrowseFragment(), ROUTE_BROWSE_TAG)
//                            .addToBackStack(null)
                            .show(routeFragment)
                            .hide(caloriesFragment)
                            .commit();
                    return true;
                case R.id.menu_item_calories:
//                    Toast.makeText(this, "routes", Toast.LENGTH_SHORT).show();
                    fragmentManager
                            .beginTransaction()
//                            .replace(R.id.frame_layout, new CaloriesFragment(), CALORIES_TAG)
//                            .addToBackStack(null)
                            .show(caloriesFragment)
                            .hide(routeFragment)
                            .commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.bottomNavigation.getSelectedItemId() == R.id.menu_item_routes) {
            if (routeFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                routeViewModel.setSelectedRoute(null);
                routeFragment.getChildFragmentManager().popBackStack();
                return;
            }
        }
        super.onBackPressed();
    }
}