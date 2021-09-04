package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

public class Workout {
    private Date date;
    private String label;
    private double distance;
    private double duration;
    private int steps;
    private List<Location> coordinates;

    public Workout() {
    }

    public Workout(Date date, String label, double distance, double duration, int steps, List<Location> coordinates) {
        this.date = date;
        this.label = label;
        this.distance = distance;
        this.duration = duration;
        this.steps = steps;
        this.coordinates = coordinates;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public List<Location> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Location> coordinates) {
        this.coordinates = coordinates;
    }
}
