package rs.ac.bg.etf.running.workouts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.RunDatabase;
import rs.ac.bg.etf.running.data.WorkoutRepository;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;

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

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);

        binding.toolbar.inflateMenu(R.menu.workout_list_options_menu);
        binding.toolbar.setOnMenuItemClickListener(menuItem -> {
            switch(menuItem.getItemId()) {
                case R.id.workout_menu_item_sort:
                    workoutViewModel.invertSorted();
                return true;
            }
            return false;
        });

        WorkoutAdapter workoutAdapter = new WorkoutAdapter();

        workoutViewModel.getWorkoutList().observe(getViewLifecycleOwner(), workoutAdapter::setWorkoutList);

        binding.recyclerView.setAdapter(workoutAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.inflate(R.menu.workout_list_fab_menu);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.workout_fab_create:
                    navController.navigate(WorkoutListFragmentDirections.createWorkout());
                    return false; //zatvaramo speed dial
                case R.id.workout_fab_start:
                    return false;
            }

            return true;
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}