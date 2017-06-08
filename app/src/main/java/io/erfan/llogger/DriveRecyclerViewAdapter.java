package io.erfan.llogger;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.erfan.llogger.activity.DriveDetailActivity;
import io.erfan.llogger.model.Drive;

public class DriveRecyclerViewAdapter extends RecyclerView.Adapter<DriveRecyclerViewAdapter.ViewHolder> {
    List<Drive> mDrives;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mViewDuration;
        public TextView mViewLocation;
        public TextView mViewTime;
        public TextView mViewCar;

        public Drive mDrive;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mViewDuration = (TextView) v.findViewById(R.id.drive_item_duration);
            mViewLocation = (TextView) v.findViewById(R.id.drive_item_location);
            mViewTime = (TextView) v.findViewById(R.id.drive_item_time);
            mViewCar = (TextView) v.findViewById(R.id.drive_item_car);

            // set on click to view monster
            mItem = (CardView) v.findViewById(R.id.drive_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openViewMonster = new Intent(v.getContext(), DriveDetailActivity.class);
                    openViewMonster.putExtra("Drive", mDrive);

                    v.getContext().startActivity(openViewMonster);
                }
            });
        }
    }

    public DriveRecyclerViewAdapter(List<Drive> drives) {
        mDrives = drives;
        Log.d("DBG", String.format("init size: %d", mDrives.size()));
    }

    public void updateList(List<Drive> drives) {
        mDrives = drives;
        notifyDataSetChanged();
    }

    @Override
    public DriveRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("DBG", "Get VH");
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drive_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("DBG", "Bind VH");
        // set the correct data in the list
        holder.mDrive = mDrives.get(position);
//        holder.mViewName.setText(mDrives.get(position).getName());
//        holder.mViewSpecies.setText(mDrives.get(position).getSpecies());
        holder.mViewDuration.setText(mDrives.get(position).getFormattedDuration());
        holder.mViewLocation.setText(mDrives.get(position).getLocation());
//        holder.mViewCar.setText(mDrives.get(position).getCar());
        holder.mViewTime.setText(mDrives.get(position).getFormattedTime());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("DBG", String.format("size: %d", mDrives.size()));
        return mDrives.size();
    }
}
