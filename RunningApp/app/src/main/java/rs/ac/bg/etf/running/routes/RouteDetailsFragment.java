package rs.ac.bg.etf.running.routes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.databinding.FragmentRouteDetailsBinding;

public class RouteDetailsFragment extends Fragment {

    private FragmentRouteDetailsBinding binding;
    private RouteViewModel routeViewModel;

    private NavController navController;
    private MainActivity mainActivity;

    public RouteDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        routeViewModel = new ViewModelProvider(mainActivity).get(RouteViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRouteDetailsBinding.inflate(inflater, container, false);

        Route selectedRoute = routeViewModel.getRoutes().get(RouteDetailsFragmentArgs.fromBundle(requireArguments()).getRouteIndex());

        binding.toolbar.setTitle(selectedRoute.getLabel());

        binding.toolbar.setNavigationOnClickListener(view -> navController.navigateUp());

        binding.routeImage.setImageDrawable(selectedRoute.getImage());
        binding.routeLabel.setText(selectedRoute.getLabel());
        binding.routeName.setText(selectedRoute.getName());
        binding.routeLength.setText(selectedRoute.getLength() + "km");
        binding.routeDifficulty.setText(selectedRoute.getDifficulty());
        binding.routeDescription.setText(selectedRoute.getDescription());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}