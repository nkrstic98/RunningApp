package rs.ac.bg.etf.running.notifications;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

import rs.ac.bg.etf.running.workouts.DateTimeUtil;
import rs.ac.bg.etf.running.workouts.WorkoutCreateFragment;

public class TimePickerFragment extends DialogFragment {

    public static class AlarmTime implements Serializable {
        private int hour;
        private int minute;

        public AlarmTime(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }
    }

    public static final String SET_TIME_KEY = "set-time-key";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(
                requireActivity(),
                (view, hourOfDay, minute) -> {
                    Bundle result = new Bundle();
                    result.putSerializable(
                            SET_TIME_KEY,
                            new AlarmTime(hourOfDay, minute));
                    getParentFragmentManager().setFragmentResult(
                            NotificationsFragment.REQUEST_KEY,
                            result);
                },
                12,
                0,
                true
        );
    }
}
