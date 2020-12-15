package rs.ac.bg.etf.running.routes;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import rs.ac.bg.etf.running.R;

public class Route {
    private final String label;
    private final String location;
    private final Drawable image;
    private final String name;
    private final double length;
    private final String difficulty;
    private final String description;

    public static Route createFromResources(Resources resources, int index) {
        String routeLabel = resources.getString(R.string.route_label);
        String[] locations = resources.getStringArray(R.array.route_locations);
        TypedArray images = resources.obtainTypedArray(R.array.route_images);
        String[] names = resources.getStringArray(R.array.route_names);
        TypedArray length = resources.obtainTypedArray(R.array.route_lengths);
        int[] difficulties = resources.getIntArray(R.array.route_difficulties);
        String[] difficultyPhrases = resources.getStringArray(R.array.route_difficulties_phrases);
        String[] descriptions = resources.getStringArray(R.array.route_descriptions);

        Route result = new Route(
                routeLabel + " " + (index + 1),
                locations[index],
                images.getDrawable(index),
                names[index],
                length.getFloat(index, 0),
                difficultyPhrases[difficulties[index]],
                descriptions[index]
        );

        images.recycle();
        length.recycle();

        return result;
    }

    public Route(
            String label, String location, Drawable image,
            String name, double length, String difficulty, String description) {
        this.label = label;
        this.location = location;
        this.image = image;
        this.name = name;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getLocation() {
        return location;
    }

    public Drawable getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDescription() {
        return description;
    }
}
