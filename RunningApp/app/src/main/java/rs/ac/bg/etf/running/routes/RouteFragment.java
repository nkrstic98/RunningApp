package rs.ac.bg.etf.running.routes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentRouteBinding;

public class RouteFragment extends Fragment {

    private FragmentRouteBinding binding;
    private RouteViewModel routeViewModel;

    private FragmentManager childFragmentManager;

    private static final String ROUTE_BROWSE_TAG = "fragment-route-brose-tag";
    private RouteBrowseFragment routeBrowseFragment;

    public RouteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}