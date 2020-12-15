package rs.ac.bg.etf.running.routes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.LifecycleAwareLogger;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentRouteBrowseBinding;

public class RouteBrowseFragment extends Fragment {

    private FragmentRouteBrowseBinding binding;
    private RouteViewModel routeViewModel;

    public RouteBrowseFragment() {
        getLifecycle().addObserver(new LifecycleAwareLogger(
                MainActivity.LOG_TAG,
                RouteBrowseFragment.class.getSimpleName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRouteBrowseBinding.inflate(inflater, container, false);

        MainActivity parentActivity = (MainActivity) getActivity();

        routeViewModel = new ViewModelProvider(parentActivity).get(RouteViewModel.class);
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            routes.add(Route.createFromResources(getResources(), i));
        }
        routeViewModel.setRoutes(routes);

        routeViewModel.getSelectedRoute().observe(this, selectedRoute -> {
            if (selectedRoute != null) {

            }
        });

        routeViewModel = new ViewModelProvider(parentActivity).get(RouteViewModel.class);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(new RouteAdapter(parentActivity));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));

        return binding.getRoot();
    }
}