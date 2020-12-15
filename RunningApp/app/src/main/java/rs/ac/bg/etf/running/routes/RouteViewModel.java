package rs.ac.bg.etf.running.routes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RouteViewModel extends ViewModel {

    private List<Route> routes;
    private MutableLiveData<Route> selectedRoute = new MutableLiveData<>(null);

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public LiveData<Route> getSelectedRoute() {
        return selectedRoute;
    }

    public void setSelectedRoute(Route selectedRoute) {
        this.selectedRoute.setValue(selectedRoute);
    }
}
