package rs.ac.bg.etf.running.routes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
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
    private MainActivity mainActivity;

    private NavController navController;

    public RouteBrowseFragment() {
//        getLifecycle().addObserver(new LifecycleAwareLogger(
//                MainActivity.LOG_TAG,
//                RouteBrowseFragment.class.getSimpleName()));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();

        routeViewModel = new ViewModelProvider(mainActivity).get(RouteViewModel.class);
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            routes.add(Route.createFromResources(getResources(), i));
        }
        routeViewModel.setRoutes(routes);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRouteBrowseBinding.inflate(inflater, container, false);

        RouteAdapter routeAdapter = new RouteAdapter(
                routeViewModel,
                routeIndex -> {
                    RouteBrowseFragmentDirections.ActionShowRouteDetails action = RouteBrowseFragmentDirections.actionShowRouteDetails();
                    action.setRouteIndex(routeIndex);
                    navController.navigate(action);
                },
                routeIndex -> {
                    String locationString = routeViewModel.getRoutes().get(routeIndex).getLocation();
                    locationString = locationString.replace(" ", "%20");
                    locationString = locationString.replace(",", "%2C");
                    Uri locationUri = Uri.parse("geo:0,0?q=" + locationString);

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(locationUri);

                    mainActivity.startActivity(intent);
                }
        );

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(routeAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //tek kada kreiramo View, kreiramo NavController
        navController = Navigation.findNavController(view);
    }


}