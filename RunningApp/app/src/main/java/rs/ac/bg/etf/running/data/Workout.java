package rs.ac.bg.etf.running.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Workout {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Date date;
    private String label;
    private double distance;
    private double duration;
}
