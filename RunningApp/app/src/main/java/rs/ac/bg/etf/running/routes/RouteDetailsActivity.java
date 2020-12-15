package rs.ac.bg.etf.running.routes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import rs.ac.bg.etf.running.databinding.ActivityRouteDetailsBinding;

public class RouteDetailsActivity extends AppCompatActivity {

    public static final String SELECTED_ROUTE_INDEX = "route-index";

    private ActivityRouteDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent invocationIntent = getIntent();
        int selectedRouteIndex = invocationIntent.getIntExtra(SELECTED_ROUTE_INDEX, 0);
        Route selectedRoute = Route.createFromResources(getResources(), selectedRouteIndex);

        binding.routeImage.setImageDrawable(selectedRoute.getImage());
        binding.routeLabel.setText(selectedRoute.getLabel());
        binding.routeName.setText(selectedRoute.getName());
        binding.routeLength.setText(selectedRoute.getLength() + "km");
        binding.routeDifficulty.setText(selectedRoute.getDifficulty());
        binding.routeDescription.setText(selectedRoute.getDescription());
    }
}