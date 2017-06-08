package io.erfan.llogger;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.erfan.llogger.model.Car;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<CarRecyclerViewAdapter.ViewHolder> {
    List<Car> mCars;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mName;
        public TextView mPlate;

        public Car mCar;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mName = (TextView) v.findViewById(R.id.car_item_name);
            mPlate = (TextView) v.findViewById(R.id.car_item_plate);

            mItem = (CardView) v.findViewById(R.id.car_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "clicked on " + mCar.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public CarRecyclerViewAdapter(List<Car> cars) {
        mCars = cars;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
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