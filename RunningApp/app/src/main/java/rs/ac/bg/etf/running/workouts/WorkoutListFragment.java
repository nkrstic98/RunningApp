package rs.ac.bg.etf.running.workouts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;
import rs.ac.bg.etf.running.dialogs.FilterDialogFragment;
import rs.ac.bg.etf.running.dialogs.SortDialogFragment;
import rs.ac.bg.etf.running.musicplayer.CustomTouchListener;
import rs.ac.bg.etf.running.musicplayer.OnItemClickListener;

@AndroidEntryPoint
public class WorkoutListFragment extends Fragment {

    private FragmentWorkoutListBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    public WorkoutListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mainActivity.getMenuInflater().inflate(R.menu.workout_list_options_menu, menu);

        menu.getItem(0).setOnMenuItemClickListener(item -> {
            SortDialogFragment dialogFragment = new SortDialogFragment(binding);
            dialogFragment.show(getChildFragmentManager(), "sort-fragment");
            return true;
        });

        menu.getItem(1).setOnMenuItemClickListener(item -> {
            FilterDialogFragment dialog = new FilterDialogFragment(binding);
            dialog.show(getChildFragmentManager(), "filter-fragment");
            return true;
        });
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);

        WorkoutAdapter workoutAdapter = new WorkoutAdapter();

        workoutViewModel.subscribeToRealtimeUpdates(workoutAdapter);

        binding.recyclerView.setAdapter(workoutAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        binding.recyclerView.addOnItemTouchListener(new CustomTouchListener(mainActivity, (view, index) -> {
            WorkoutListFragmentDirections.ActionWorkoutListToGoogleMapsFragment action =
                    WorkoutListFragmentDirections.actionWorkoutListToGoogleMapsFragment(index);
            navController.navigate(action);
        }));

        binding.floatingActionButton.inflate(R.menu.workout_list_fab_menu);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.workout_fab_create:
                    navController.navigate(WorkoutListFragmentDirections.createWorkout());
                    return false;
            }

            return true;
        });

        if(workoutViewModel.getDistanceFilterApplied()) {
            binding.chipDistanceFilter.setVisibility(View.VISIBLE);
            binding.chipDistanceFilter.setText(workoutViewModel.getDistanceFilter());
            binding.chipDistanceFilter.setOnCloseIconClickListener(v -> {
                workoutViewModel.resetDistanceFilter();
            });
        }
        else {
            binding.chipDistanceFilter.setVisibility(View.GONE);
        }

        if(workoutViewModel.getDurationFilterApplied()) {
            binding.chipDurationFilter.setVisibility(View.VISIBLE);
            binding.chipDurationFilter.setText(workoutViewModel.getDurationFilter());
            binding.chipDurationFilter.setOnCloseIconClickListener(v -> {
                workoutViewModel.resetDurationFilter();
            });
        }
        else {
            binding.chipDurationFilter.setVisibility(View.GONE);
        }

        if(workoutViewModel.getSortApplied()) {
            binding.chipSort.setVisibility(View.VISIBLE);
            binding.chipSort.setText(workoutViewModel.getSortCriteria());
            binding.chipSort.setOnCloseIconClickListener(v -> {
                workoutViewModel.removeSort();
            });
        }
        else {
            binding.chipSort.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


}