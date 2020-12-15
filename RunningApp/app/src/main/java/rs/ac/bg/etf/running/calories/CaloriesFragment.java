package rs.ac.bg.etf.running.calories;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentCaloriesBinding;

public class CaloriesFragment extends Fragment {

    private FragmentCaloriesBinding binding;

    private CaloriesViewModel caloriesViewModel;

    public CaloriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCaloriesBinding.inflate(inflater, container, false);

        caloriesViewModel = new ViewModelProvider(this).get(CaloriesViewModel.class);
        caloriesViewModel.initByInstanceStateBundle(savedInstanceState);

        MainActivity parentActivity = (MainActivity) getActivity();

        caloriesViewModel.getCaloriesBurned().observe(this, caloriesBurned -> {
            if (caloriesBurned != -1) {
                String prefix = getResources().getString(R.string.calories_burned);
                binding.burned.setText(prefix + ": " + caloriesBurned + " kcal");
            }
        });

        caloriesViewModel.getCaloriesNeeded().observe(this, caloriesNeeded -> {
            if (caloriesNeeded != -1) {
                String prefix = getResources().getString(R.string.calories_needed);
                binding.needed.setText(prefix + ": " + caloriesNeeded + " kcal");
            }
        });

        String[] metStrings = getResources().getStringArray(R.array.met_strings);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                parentActivity,
                android.R.layout.simple_list_item_1,
                metStrings);
        binding.spinner.setAdapter(arrayAdapter);

        binding.calculate.setOnClickListener(v -> {
            double weight = 0;
            try {
                weight = Double.parseDouble(
                        binding.weight.getEditText().getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(
                        parentActivity,
                        R.string.calories_error_message,
                        Toast.LENGTH_SHORT).show();
                binding.weight.getEditText().requestFocus();
                return;
            }

            double height = 0;
            try {
                height = Double.parseDouble(
                        binding.height.getEditText().getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(
                        parentActivity,
                        R.string.calories_error_message,
                        Toast.LENGTH_SHORT).show();
                binding.height.getEditText().requestFocus();
                return;
            }

            int age = 0;
            try {
                age = Integer.parseInt(
                        binding.age.getEditText().getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(
                        parentActivity,
                        R.string.calories_error_message,
                        Toast.LENGTH_SHORT).show();
                binding.age.getEditText().requestFocus();
                return;
            }

            if (binding.radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(
                        parentActivity,
                        R.string.calories_error_message,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isMale = binding.male.isChecked();

            double duration = 0;
            try {
                duration = Double.parseDouble(
                        binding.duration.getEditText().getText().toString());
            } catch (NumberFormatException nfe) {
                Toast.makeText(
                        parentActivity,
                        R.string.calories_error_message,
                        Toast.LENGTH_SHORT).show();
                binding.duration.getEditText().requestFocus();
                return;
            }

            TypedArray metValues = getResources().obtainTypedArray(R.array.met_values);
            double met = metValues.getFloat(binding.spinner.getSelectedItemPosition(), 0);

            caloriesViewModel.updateValues(weight, height, age, isMale, duration, met);
        });

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(
                CaloriesViewModel.CALORIES_BURNED_KEY,
                caloriesViewModel.getCaloriesBurned().getValue());
        outState.putInt(
                CaloriesViewModel.CALORIES_NEEDED_KEY,
                caloriesViewModel.getCaloriesNeeded().getValue());
    }
}