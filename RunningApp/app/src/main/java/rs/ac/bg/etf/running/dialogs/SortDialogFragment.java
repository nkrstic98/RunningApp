package rs.ac.bg.etf.running.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

public class SortDialogFragment extends DialogFragment {

    private String sort_conditions[] = { "date", "distance", "duration" };

    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;

    private int chosenCondition = -1;

    public SortDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = mainActivity.getLayoutInflater();

        builder
                .setTitle("Sort workouts")
                .setView(inflater.inflate(R.layout.dialog_sort_filter, null))
                .setSingleChoiceItems(R.array.workouts_sort, -1, (dialog, which) -> {
                    chosenCondition = which;
                })
                .setPositiveButton("Sort", (dialog, which) -> {
                    if(chosenCondition != -1) {
                        workoutViewModel.sortWorkouts(sort_conditions[chosenCondition]);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                });

        return builder.create();
    }
}