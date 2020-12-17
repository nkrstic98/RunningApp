package rs.ac.bg.etf.running.workouts;

import android.app.DatePickerDialog;
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

import java.util.Date;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutCreateBinding;

public class WorkoutCreateFragment extends Fragment {

    public static final String REQUEST_KEY = "date-picker-request";

    private FragmentWorkoutCreateBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    public WorkoutCreateFragment() {
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
        binding = FragmentWorkoutCreateBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(
                view -> navController.navigateUp());

        binding.workoutDateEditText.setOnClickListener(
                view -> new DatePickerFragment().show(getChildFragmentManager(), null));

        getChildFragmentManager().setFragmentResultListener(REQUEST_KEY, this,
                (requestKey, result) -> {
                    Date date = (Date) result.getSerializable(DatePickerFragment.SET_DATE_KEY);
                    String dateForEditText = DateTimeUtil.getSimpleDateFormat().format(date);
                    binding.workoutDate.getEditText().setText(dateForEditText);
                });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}