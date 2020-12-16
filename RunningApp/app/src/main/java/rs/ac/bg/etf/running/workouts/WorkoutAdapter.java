package rs.ac.bg.etf.running.workouts;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rs.ac.bg.etf.running.databinding.ViewHolderWorkoutBinding;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    public WorkoutAdapter() {

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
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderWorkoutBinding binding;

        public WorkoutViewHolder(@NonNull ViewHolderWorkoutBinding workoutBinding) {
            super(workoutBinding.getRoot());
            binding = workoutBinding;
        }
    }
}
