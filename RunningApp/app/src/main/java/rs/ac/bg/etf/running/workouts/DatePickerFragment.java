package rs.ac.bg.etf.running.workouts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static final String SET_DATE_KEY = "set-date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int initYear = calendar.get(Calendar.YEAR);
        int initMonth = calendar.get(Calendar.MONTH);
        int initDay = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(
                requireActivity(),
                (view, setYear, setMonth, setDay) -> {
                    Bundle result = new Bundle();
                    result.putSerializable(
                            SET_DATE_KEY,
                            DateTimeUtil.getDate(setYear, setMonth, setDay));
                    getParentFragmentManager().setFragmentResult(
                            WorkoutCreateFragment.REQUEST_KEY,
                            result);
                },
                initYear, initMonth, initDay);
    }
}
