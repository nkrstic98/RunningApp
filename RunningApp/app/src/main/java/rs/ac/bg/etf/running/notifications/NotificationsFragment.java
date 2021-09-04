package rs.ac.bg.etf.running.notifications;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Notification;
import rs.ac.bg.etf.running.databinding.FragmentNotificationsBinding;
import rs.ac.bg.etf.running.workouts.DatePickerFragment;
import rs.ac.bg.etf.running.workouts.DateTimeUtil;

import static rs.ac.bg.etf.running.data.Notification.DAYS_OF_WEEK;

public class NotificationsFragment extends Fragment {

    public static final String REQUEST_KEY = "time-picker-key";

    private MainActivity mainActivity;
    private FragmentNotificationsBinding binding;

    private CollectionReference notificationReference;
    private String notificationId = "";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isPermissionGranted -> {
                        if(isPermissionGranted) {
                            setReminder();
                        }
                        else {
                            Toast.makeText(
                                    mainActivity,
                                    "You must allow device to always access your location in order for Nootifications to work!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    private boolean []days_selected = { false, false, false, false, false, false, false };

    private TimePickerFragment.AlarmTime alarmTime;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();

        notificationReference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("notifications");

        createNotificationChannel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        notificationReference
                .get()
                .addOnSuccessListener(mainActivity, queryDocumentSnapshots -> {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    if(list.size() > 0) {
                        notificationId = list.get(0).getId();
                        Notification notification = list.get(0).toObject(Notification.class);

                        for(int i = 0; i < DAYS_OF_WEEK.length; i++) {
                            if(notification.getDays().contains(DAYS_OF_WEEK[i])) {
//                                days_selected[i] = true;
                                toggleDays(i + 1);
                            }
                        }

                        alarmTime = new TimePickerFragment.AlarmTime(12, 0);
                        alarmTime.setHour(notification.getHours());
                        alarmTime.setMinute(notification.getMinutes());
                        binding.alarmTimeEditText.setText(notification.getTime());

                        binding.buttonSubmit.setText("Update reminder");
                        binding.buttonRemoveReminder.setEnabled(true);
                    }
                    else {
                        binding.buttonRemoveReminder.setEnabled(false);
                    }
                });

        binding.alarmTimeEditText.setOnClickListener(v -> new TimePickerFragment().show(getChildFragmentManager(), null));

        getChildFragmentManager().setFragmentResultListener(REQUEST_KEY, this,
                (requestKey, result) -> {
                    alarmTime = (TimePickerFragment.AlarmTime) result.getSerializable(TimePickerFragment.SET_TIME_KEY);

                    StringBuilder sb = new StringBuilder();
                    sb.append(alarmTime.getHour() < 10 ? "0" + alarmTime.getHour() : alarmTime.getHour());
                    sb.append(":");
                    sb.append(alarmTime.getMinute() < 10 ? "0" + alarmTime.getMinute() : alarmTime.getMinute());

                    binding.alarmTime.getEditText().setText(sb.toString());
                });

        binding.buttonSubmit.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            else {
                setReminder();
            }
        });

        binding.buttonRemoveReminder.setOnClickListener(v -> {
            removeReminder();
        });

        binding.monday.setOnClickListener(v -> {
            toggleDays(2);
        });
        binding.tuesday.setOnClickListener(v -> {
            toggleDays(3);
        });
        binding.wednesday.setOnClickListener(v -> {
            toggleDays(4);
        });
        binding.thursday.setOnClickListener(v -> {
            toggleDays(5);
        });
        binding.friday.setOnClickListener(v -> {
            toggleDays(6);
        });
        binding.saturday.setOnClickListener(v -> {
            toggleDays(7);
        });
        binding.sunday.setOnClickListener(v -> {
            toggleDays(1);
        });

        return binding.getRoot();
    }

    private void setReminder() {
        if(binding.alarmTimeEditText.getText().toString().equals("")) {
            Toast.makeText(mainActivity, "Choose time of day for your notification!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(mainActivity, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

        for(int i = 0; i < days_selected.length; i++) {
            if(days_selected[i]) {
                Toast.makeText(mainActivity, "Reminder set!", Toast.LENGTH_SHORT).show();

                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        getTime(i + 1),
                        pendingIntent
                );
            }
        }

        List<String> days = new ArrayList<>();
        for(int i = 0; i < days_selected.length; i++) {
            if(days_selected[i]) {
                days.add(DAYS_OF_WEEK[i]);
            }
        }

        if(notificationId.equals("")) {
            notificationReference.add(new Notification(days, alarmTime.getHour(), alarmTime.getMinute(), true));
        }
        else {
            notificationReference
                    .document(notificationId)
                    .update(
                            "hours", alarmTime.getHour(),
                            "minutes", alarmTime.getMinute(),
                            "days", days
                    );
        }
    }

    private void removeReminder() {
        for(int i = 0; i < days_selected.length; i++) {
            if(days_selected[i]) toggleDays(i + 1);
        }
        binding.alarmTimeEditText.setText("");
        binding.buttonSubmit.setText("Set Reminder");
        binding.buttonRemoveReminder.setEnabled(false);

        notificationReference
                .document(notificationId)
                .delete();

        Intent intent = new Intent(mainActivity, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "NotificationAlarmChannel";
            String description = "Channel for Alarm Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NotificationBroadcast.ALARM_NOTIFICATION_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mainActivity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private long getTime(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, alarmTime.getHour());
        calendar.set(Calendar.MINUTE, alarmTime.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1);
        }

        return calendar.getTimeInMillis();
    }

    private void toggleDays(int num) {
        days_selected[num - 1] = !days_selected[num - 1];

        switch(num)
        {
            case 2:
                binding.monday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 3:
                binding.tuesday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 4:
                binding.wednesday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 5:
                binding.thursday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 6:
                binding.friday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 7:
                binding.saturday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
            case 1:
                binding.sunday.setBackgroundColor(days_selected[num - 1] ? getResources().getColor(R.color.day_of_week_color) : Color.WHITE);
                break;
        }
    }
}