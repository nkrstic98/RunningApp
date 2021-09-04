package rs.ac.bg.etf.running.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.running.data.Audio;

public class StorageUtil {

    private final String STORAGE = "rs.ac.bg.etf.running.STORAGE";
    private SharedPreferences sharedPreferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    public void storeAudio(List<Audio> arrayList) {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.commit();
    }

    public ArrayList<Audio> loadAudio() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("audioArrayList", null);
        Type type = new TypeToken<List<Audio>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void storeAudioIndex(int index) {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("audioIndex", index);
        editor.commit();
    }

    public int loadAudioindex() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("audioIndex", -1);
    }

    public void clearCachedAudioPlaylist() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
