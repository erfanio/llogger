package io.erfan.llogger.recycleradapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.erfan.llogger.R;
import io.erfan.llogger.model.Car;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<CarRecyclerViewAdapter.ViewHolder> {
    private List<Car> mCars;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mPlate;

        public Car mCar;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mName = (TextView) v.findViewById(R.id.car_item_name);
            mPlate = (TextView) v.findViewById(R.id.car_item_plate);
        }
    }

    public CarRecyclerViewAdapter(List<Car> cars) {
        mCars = cars;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_car, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set the correct data in the list
        holder.mCar = mCars.get(position);
        holder.mName.setText(mCars.get(position).getName());
        holder.mPlate.setText(mCars.get(position).getPlateNo());

    }

    public void updateList(List<Car> cars) {
        mCars = cars;
        notifyDataSetChanged();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCars.size();
    }
}
