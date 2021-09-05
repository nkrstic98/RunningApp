package rs.ac.bg.etf.running.workouts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentGoogleMapsBinding;

public class GoogleMapsFragment extends Fragment {

    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;
    private FragmentGoogleMapsBinding binding;

    private MutableLiveData<Integer> workoutIndex = new MutableLiveData<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        workoutIndex.setValue(GoogleMapsFragmentArgs.fromBundle(getArguments()).getWorkoutIndex());
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            double lat = workoutViewModel.getPath(workoutIndex.getValue()).get(0).getLatitude();
            double lon = workoutViewModel.getPath(workoutIndex.getValue()).get(0).getLongitude();
            LatLng sydney = new LatLng(lat, lon);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lon))
                    .zoom(7)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            PolylineOptions polylineOptions = new PolylineOptions();
            for (Location l:
                 workoutViewModel.getPath(workoutIndex.getValue())) {
                polylineOptions.add(new LatLng(l.getLatitude(), l.getLongitude()));
            }

            polylineOptions
                    .color(Color.RED)
                    .width(4);

            Polyline polyline = googleMap.addPolyline(polylineOptions);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentGoogleMapsBinding.inflate(inflater, container, false);

        Workout workout = workoutViewModel.getWorokout(workoutIndex.getValue());

        binding.workoutDate.setText(DateTimeUtil.getSimpleDateTimeFormat().format(workout.getDate()));
        binding.workoutLabel.setText(workout.getLabel());
        binding.workoutDistance.setText(String.format("%.2f km", workout.getDistance()));
        binding.workoutPace.setText(String.format("%s min/km", DateTimeUtil.realMinutesToString(workout.getDuration() / workout.getDistance())));
        binding.workoutDuration.setText(String.format("%s min", DateTimeUtil.realMinutesToString(workout.getDuration())));
        binding.workoutSteps.setText(String.format("%s steps", workout.getSteps()));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}