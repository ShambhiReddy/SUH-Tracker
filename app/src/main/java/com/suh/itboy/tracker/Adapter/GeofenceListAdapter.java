package com.suh.itboy.tracker.Adapter;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suh.itboy.tracker.Model.GeofenceListItemModel;
import com.suh.itboy.tracker.Provider.Contract.AppContract;
import com.suh.itboy.tracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itboy on 10/8/2015.
 */
public class GeofenceListAdapter extends RecyclerView.Adapter<GeofenceListAdapter.ViewHolder> {
    List<GeofenceListItemModel> mGeofenceList;

    public GeofenceListAdapter() {
        mGeofenceList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.geofence_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GeofenceListItemModel item = mGeofenceList.get(position);
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getLatitude() + ", " + item.getLongitude());
    }

    @Override
    public int getItemCount() {
        return mGeofenceList.size();
    }

    public int remove(int adapterPosition, ContentResolver contentResolver) {
        int returnValue = contentResolver.delete(AppContract.GeofenceEntry.CONTENT_URI,
                AppContract.GeofenceEntry._ID + " = ?",
                new String[]{String.valueOf(mGeofenceList.get(adapterPosition).getId())});
        if (returnValue != 0) {
            mGeofenceList.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
        }

        return returnValue;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subTitle;
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

        }
    }

    /*@Override
    public long getItemId(int position) {
        return mEventList.get(position).getId();
    }*/

    public void swapCursor(Cursor cursor) {
        mGeofenceList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GeofenceListItemModel item = new GeofenceListItemModel(
                        cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_TITLE)),
                        cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(AppContract.LocationEntry.COLUMN_LONGITUDE))
                );
                item.setId(cursor.getLong(cursor.getColumnIndex(AppContract.GeofenceEntry._ID)));
                item.setRequestId(cursor.getString(cursor.getColumnIndex(AppContract.GeofenceEntry.COLUMN_REQUEST_ID)));
                mGeofenceList.add(item);
            }
        }

        this.notifyDataSetChanged();
    }
}
