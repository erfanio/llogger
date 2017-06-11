package io.erfan.llogger.recycleradapters;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.activity.DriveDetailActivity;
import io.erfan.llogger.model.Drive;

public class DriveRecyclerViewAdapter extends RecyclerView.Adapter<DriveRecyclerViewAdapter.ViewHolder> {
    private List<Drive> mDrives;

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

            // set on click to view driver details
            mItem = (CardView) v.findViewById(R.id.drive_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openViewDrive = new Intent(v.getContext(), DriveDetailActivity.class);
                    openViewDrive.putExtra("Drive", mDrive);

                    v.getContext().startActivity(openViewDrive);
                }
            });
        }

        // binding logic is here to be able to use the viewholder outside of recycler view too
        public void bind(Drive drive) {
            mDrive = drive;
            mViewDuration.setText(drive.getFormattedDuration());
            mViewLocation.setText(drive.getLocation());
//            mViewCar.setText(drive.getCar());
            mViewTime.setText(drive.getFormattedTime());
        }
    }

    public DriveRecyclerViewAdapter(List<Drive> drives) {
        mDrives = drives;
    }

    public void updateList(List<Drive> drives) {
        mDrives = drives;
        notifyDataSetChanged();
    }

    @Override
    public DriveRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_drive, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set the correct data in the list
        holder.bind(mDrives.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDrives.size();
    }
}
