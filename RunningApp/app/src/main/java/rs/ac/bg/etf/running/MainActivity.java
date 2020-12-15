package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import rs.ac.bg.etf.running.calories.CaloriesActivity;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.routes.RouteBrowseActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonCalories.setOnClickListener(view -> {
            Intent explicitIntent = new Intent();
            explicitIntent.setClass(this, CaloriesActivity.class);
            startActivity(explicitIntent);
        });

        binding.buttonRoute.setOnClickListener(view -> {
            Intent explicitIntent = new Intent();
            explicitIntent.setClass(this, RouteBrowseActivity.class);
            startActivity(explicitIntent);
        });

    }
}