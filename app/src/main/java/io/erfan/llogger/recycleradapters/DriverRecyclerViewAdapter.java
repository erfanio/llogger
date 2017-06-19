package io.erfan.llogger.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.model.Driver;

public class DriverRecyclerViewAdapter extends RecyclerView.Adapter<DriverRecyclerViewAdapter.ViewHolder> {
    private List<Driver> mDrivers;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mName = (TextView) v.findViewById(R.id.driver_item_name);
        }
    }

    public DriverRecyclerViewAdapter(List<Driver> drivers) {
        mDrivers = drivers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_driver, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set the correct data in the list
        holder.mName.setText(mDrivers.get(position).getName());

    }

    public void updateList(List<Driver> drivers) {
        mDrivers = drivers;
        notifyDataSetChanged();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDrivers.size();
    }
}
