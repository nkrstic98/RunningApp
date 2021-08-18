package rs.ac.bg.etf.running.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.icu.number.NumberFormatter;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Currency;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.DialogFilterBinding;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

public class FilterDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private WorkoutViewModel workoutViewModel;

    private DialogFilterBinding binding;
    private FragmentWorkoutListBinding workoutListBinding;

    private AlertDialog dialog;

    MutableLiveData<Double> distance_min = new MutableLiveData<Double>(0.0);
    MutableLiveData<Double> distance_max = new MutableLiveData<Double>(100.0);
    MutableLiveData<Double> duration_min = new MutableLiveData<Double>(0.0);
    MutableLiveData<Double> duration_max = new MutableLiveData<Double>(360.0);

    public FilterDialogFragment(FragmentWorkoutListBinding binding) {
        workoutListBinding = binding;
    }

    public FilterDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = mainActivity.getLayoutInflater();

        builder
                .setTitle("Filter workouts")
                .setView(inflater.inflate(R.layout.dialog_filter, null))
                .setPositiveButton("Filter", (dialog, which) -> {
                    workoutViewModel.filterWorkouts(distance_min.getValue(), distance_max.getValue(), duration_min.getValue(), duration_max.getValue());
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dismiss();
                });

        dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            RangeSlider durationSlider = dialog.findViewById(R.id.run_duration);
            RangeSlider distanceSlider = dialog.findViewById(R.id.run_distance);

//            durationSlider.setValueFrom((float)workoutViewModel.getMin_duration());
//            durationSlider.setValueTo((float) workoutViewModel.getMax_duration());
//
//            distanceSlider.setValueFrom((float)workoutViewModel.getMin_distance());
//            distanceSlider.setValueTo((float) workoutViewModel.getMax_distance());

            durationSlider.addOnChangeListener((slider, value, fromUser) -> {
                duration_min.setValue((double) slider.getValues().get(0));
                duration_max.setValue((double) slider.getValues().get(1));
            });

            distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
                distance_min.setValue((double) slider.getValues().get(0));
                distance_max.setValue((double) slider.getValues().get(1));
            });
        });


        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if(workoutViewModel.getDistanceFilterApplied()) {
            workoutListBinding.chipDistanceFilter.setVisibility(View.VISIBLE);
            workoutListBinding.chipDistanceFilter.setText(workoutViewModel.getDistanceFilter());
            workoutListBinding.chipDistanceFilter.setOnCloseIconClickListener(v -> {
                workoutViewModel.resetDistanceFilter();
                workoutListBinding.chipDistanceFilter.setVisibility(View.GONE);
            });
        }

        if(workoutViewModel.getDurationFilterApplied()) {
            workoutListBinding.chipDurationFilter.setVisibility(View.VISIBLE);
            workoutListBinding.chipDurationFilter.setText(workoutViewModel.getDurationFilter());
            workoutListBinding.chipDurationFilter.setOnCloseIconClickListener(v -> {
                workoutViewModel.resetDurationFilter();
                workoutListBinding.chipDurationFilter.setVisibility(View.GONE);
            });
        }
    }
}
