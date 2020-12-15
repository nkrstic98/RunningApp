package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import rs.ac.bg.etf.running.calories.CaloriesFragment;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "fragment-example";

    private ActivityMainBinding binding;

    private CaloriesFragment caloriesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        caloriesFragment = new CaloriesFragment();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_item_routes:
                    Toast.makeText(this, "routes", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_item_calories:
                    Toast.makeText(this, "routes", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        });
    }
}