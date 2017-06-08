package io.erfan.llogger;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.erfan.llogger.model.Driver;

public class DriverRecyclerViewAdapter extends RecyclerView.Adapter<DriverRecyclerViewAdapter.ViewHolder> {
    private List<Driver> mDrivers;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mName;

        public Driver mDriver;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mName = (TextView) v.findViewById(R.id.driver_item_name);

            mItem = (CardView) v.findViewById(R.id.driver_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "clicked on " + mDriver.getName(), Toast.LENGTH_SHORT).show();
                }
            });
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
        holder.mDriver = mDrivers.get(position);
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
