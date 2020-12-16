package rs.ac.bg.etf.running;

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
    }
}