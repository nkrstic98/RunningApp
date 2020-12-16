package rs.ac.bg.etf.running.routes;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rs.ac.bg.etf.running.databinding.ViewHolderRouteBinding;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    public interface Callback<T> {
        void invoke(T parameter);
    }

    private final RouteViewModel routeViewModel;
    private final Callback<Integer> callbackDescription;
    private final Callback<Integer> callbackLocation;

    public RouteAdapter(RouteViewModel routeViewModel, Callback<Integer> callbackDescription, Callback<Integer> callbackLocation) {
        this.routeViewModel = routeViewModel;
        this.callbackDescription = callbackDescription;
        this.callbackLocation = callbackLocation;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewHolderRouteBinding viewHolderRouteBinding = ViewHolderRouteBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new RouteViewHolder(viewHolderRouteBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeViewModel.getRoutes().get(position);
        ViewHolderRouteBinding binding = holder.binding;

        binding.routeImage.setImageDrawable(route.getImage());
        binding.routeLabel.setText(route.getLabel());
        binding.routeName.setText(route.getName());
        binding.routeLength.setText(route.getLength() + "km");
        binding.routeDifficulty.setText(route.getDifficulty());
    }

    @Override
    public int getItemCount() {
        return routeViewModel.getRoutes().size();
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder {

        public ViewHolderRouteBinding binding;

        public RouteViewHolder(@NonNull ViewHolderRouteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.routeButtonDescription.setOnClickListener(view -> {
                int routeIndex = getAdapterPosition();
                callbackDescription.invoke(routeIndex);
            });

            binding.routeButtonLocation.setOnClickListener(view -> {
                int routeIndex = getAdapterPosition();
                callbackLocation.invoke(routeIndex);
            });
        }
    }
}