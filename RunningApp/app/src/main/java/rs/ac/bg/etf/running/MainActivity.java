package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.account.AccountActivity;
import rs.ac.bg.etf.running.account.LoginFragment;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.firebase.FirebaseAuthInstance;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_NOTIFICATION = "rs.ac.bg.etf.running.NOTIFICATION";
    public static final String INTENT_ACTION_REGISTRATION = "rs.ac.bg.etf.running.REGISTRATION";
    public static final String INTENT_ACTION_LOGGING = "rs.ac.bg.etf.running.LOGGING";

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfiguration;

    private FirebaseAuth firebaseAuth = FirebaseAuthInstance.getInstance();

    private MutableLiveData<Boolean> keepLoggedIn = new MutableLiveData<>(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar_main));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = binding.navView;
        DrawerLayout drawerLayout = binding.drawerLayout;

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.route_browse,
                R.id.calories,
                R.id.workout_list
        )
        .setDrawerLayout(drawerLayout)
        .build();

        binding.navView.getMenu().findItem(R.id.menu_logout).setOnMenuItemClickListener(item -> {
            firebaseAuth.signOut();

            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
            finish();

            return true;
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        if(savedInstanceState == null) {
//            setupBottomNavigation();
//        }

        if(getIntent().getAction().equals(INTENT_ACTION_NOTIFICATION)) {
            NavController navController1 = BottomNavigationUtil.changeNavHostFragment(R.id.bottom_navigation_workouts);
            if(navController1 != null) {
                navController1.navigate(WorkoutListFragmentDirections.startWorkout());
            }
        }

//        if(getIntent().getAction().equals(INTENT_ACTION_REGISTRATION)) {
//            NavController navController = BottomNavigationUtil.changeNavHostFragment(R.id.bottom_navigation_routes);
//            if(navController != null) {
//                navController.navigate(WorkoutListFragmentDirections.startWorkout());
//            }
//        }

        if(getIntent().getAction().equals(INTENT_ACTION_LOGGING)) {
            boolean login = getIntent().getBooleanExtra(LoginFragment.KEEP_LOGGED_IN, false);
            keepLoggedIn.setValue(login);
        }

        setUserData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!keepLoggedIn.getValue()) {
            firebaseAuth.signOut();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        setupBottomNavigation();
    }

    private void setUserData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        View headerView = binding.navView.getHeaderView(0);

        TextView name = (TextView) headerView.findViewById(R.id.user_name);
        name.setText(user.getDisplayName());
        TextView email = (TextView) headerView.findViewById(R.id.user_email);
        email.setText(user.getEmail());
        ImageView profile = (ImageView) headerView.findViewById(R.id.imageProfile);
        if(user.getPhotoUrl() != null) {
            profile.setImageURI(user.getPhotoUrl());
        }
    }

//    private void setupBottomNavigation() {
//        int[] navResourceIds = new int[] {
//                R.navigation.nav_graph_routes,
//                R.navigation.nav_graph_workouts,
//                R.navigation.nav_graph_calories
//        };
//
//        BottomNavigationUtil.setup(
//                binding.bottomNavigation,
//                getSupportFragmentManager(),
//                navResourceIds,
//                R.id.nav_host_container
//        );
//    }
}