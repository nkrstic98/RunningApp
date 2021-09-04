package rs.ac.bg.etf.running.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Notification {
    public static final String[] DAYS_OF_WEEK = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    private List<String> days;
    private int hours;
    private int minutes;
    private boolean active;

    public Notification() {
    }

    public Notification(List<String> days, int hours, int minutes, boolean active) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.active = active;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTime() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHours() < 10 ? "0" + getHours() : getHours());
        sb.append(":");
        sb.append(getMinutes() < 10 ? "0" + getMinutes() : getMinutes());

        return sb.toString();
    }
}
