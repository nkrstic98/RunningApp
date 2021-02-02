package rs.ac.bg.etf.running.workouts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.ViewHolderWorkoutBinding;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList = new ArrayList<>();

    public WorkoutAdapter() {

    }

    public void setWorkoutList(List<Workout> workoutList) {
        this.workoutList = workoutList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderWorkoutBinding workoutBinding = ViewHolderWorkoutBinding.inflate(layoutInflater, parent, false);
        return new WorkoutViewHolder(workoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        ViewHolderWorkoutBinding workoutBinding = holder.binding;
        holder.bind(workoutList.get(position));
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderWorkoutBinding binding;

        public WorkoutViewHolder(@NonNull ViewHolderWorkoutBinding workoutBinding) {
            super(workoutBinding.getRoot());
            binding = workoutBinding;
        }

        public void bind(Workout workout) {
            binding.workoutDate.setText(DateTimeUtil.getSimpleDateFormat().format(workout.getDate()));
            binding.workoutLabel.setText(workout.getLabel());
            binding.workoutDistance.setText(String.format("%.2f km", workout.getDistance()));
            binding.workoutPace.setText(String.format("%s min/km", DateTimeUtil.realMinutesToString(workout.getDuration() / workout.getDistance())));
            binding.workoutDuration.setText(String.format("%s min", DateTimeUtil.realMinutesToString(workout.getDuration())));
        }
    }
}
