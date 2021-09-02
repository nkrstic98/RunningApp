package rs.ac.bg.etf.running.musicplayer;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseViewHolder> {
    public void deleteItem(int position) {

    }

    public void showUndoSnackbar() {

    }

    public void undoDelete() {

    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
