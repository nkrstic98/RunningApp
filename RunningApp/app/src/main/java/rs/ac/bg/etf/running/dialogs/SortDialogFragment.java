package rs.ac.bg.etf.running.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

public class SortDialogFragment extends DialogFragment {

    private String sort_conditions[] = { "date", "distance", "duration" };

    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;

    private FragmentWorkoutListBinding workoutListBinding;

    private int chosenCondition = -1;

    public SortDialogFragment() {
        // Required empty public constructor
    }

    public SortDialogFragment(FragmentWorkoutListBinding workoutListBinding) {
        this.workoutListBinding = workoutListBinding;
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if(workoutViewModel.getSortApplied()) {
            workoutListBinding.chipSort.setVisibility(View.VISIBLE);
            workoutListBinding.chipSort.setText(workoutViewModel.getSortCriteria());
            workoutListBinding.chipSort.setOnCloseIconClickListener(v -> {
                workoutViewModel.removeSort();
                workoutListBinding.chipSort.setVisibility(View.GONE);
            });
        }
    }
}