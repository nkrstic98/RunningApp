package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import rs.ac.bg.etf.running.calories.CaloriesFragment;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.routes.RouteBrowseFragment;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "fragment-example";

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private static final String CALORIES_TAG = "fragment-calories-tag";
    private CaloriesFragment caloriesFragment;

    private static final String ROUTE_BROWSE_TAG = "fragment-route-browse-tag";
    private RouteBrowseFragment routeBrowseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getFragments().size() == 0) {
            caloriesFragment = new CaloriesFragment();
            routeBrowseFragment = new RouteBrowseFragment();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_layout, caloriesFragment, CALORIES_TAG)
                    .add(R.id.frame_layout, routeBrowseFragment, ROUTE_BROWSE_TAG)
                    .hide(caloriesFragment)
                    .show(routeBrowseFragment)
                    .commit();
        } else {
            caloriesFragment = (CaloriesFragment) fragmentManager
                    .findFragmentByTag(CALORIES_TAG);
            routeBrowseFragment = (RouteBrowseFragment) fragmentManager
                    .findFragmentByTag(ROUTE_BROWSE_TAG);
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_routes:
//                    Toast.makeText(this, "routes", Toast.LENGTH_SHORT).show();
                    fragmentManager
                            .beginTransaction()
//                            .replace(R.id.frame_layout, new RouteBrowseFragment(), ROUTE_BROWSE_TAG)
//                            .addToBackStack(null)
                            .show(routeBrowseFragment)
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
                            .hide(routeBrowseFragment)
                            .commit();
                    return true;
            }
            return false;
        });
    }
}