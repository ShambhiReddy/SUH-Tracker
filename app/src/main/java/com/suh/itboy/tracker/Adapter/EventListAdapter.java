package com.suh.itboy.tracker.Adapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suh.itboy.tracker.Model.EventItemModel;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itboy on 10/13/2015.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    List<EventItemModel> mEventList;

    public EventListAdapter() {
        mEventList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventItemModel item = mEventList.get(position);
        holder.title.setText(item.getGeofenceListItemModel().getTitle());
        holder.subTitle.setText(item.getTransitionString());
        holder.accuracy.setText(String.valueOf(item.getTriggerLocation().getAccuracy()));
        holder.time.setText(item.getCreateTime());
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public int remove(int adapterPosition, ContentResolver contentResolver) {
        int returnValue = contentResolver.delete(AppContract.EventEntry.CONTENT_URI,
                AppContract.GeofenceEntry._ID + " = ?",
                new String[]{String.valueOf(mEventList.get(adapterPosition).getId())});
        if (returnValue != 0) {
            mEventList.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
        }

        return returnValue;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
        TextView accuracy;
        TextView time;
        public ViewHolder(final View itemView) {
            super(itemView);

/*
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemView.setSelected(!itemView.isSelected());
                    return true;
                }
            });
*/

            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            accuracy = (TextView) itemView.findViewById(R.id.accuracy);
            time = (TextView) itemView.findViewById(R.id.time);

        }
    }

    /*@Override
    public long getItemId(int position) {
        return mEventList.get(position).getId();
    }*/

    public void swapCursor(Cursor cursor) {
        mEventList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                EventItemModel item = new EventItemModel();
                item.parseFromCursor(cursor);
                mEventList.add(item);
            }
        }

        this.notifyDataSetChanged();
    }
}
