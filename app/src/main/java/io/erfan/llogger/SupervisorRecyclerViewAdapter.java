package io.erfan.llogger;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.erfan.llogger.model.Supervisor;

public class SupervisorRecyclerViewAdapter extends RecyclerView.Adapter<SupervisorRecyclerViewAdapter.ViewHolder> {
    List<Supervisor> mSupervisors;

    // provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mName;
        public TextView mLicence;

        public Supervisor mSupervisor;

        public ViewHolder(View v) {
            super(v);

            // setup ui widgets
            mName = (TextView) v.findViewById(R.id.supervisor_item_name);
            mLicence = (TextView) v.findViewById(R.id.supervisor_item_licence);

            mItem = (CardView) v.findViewById(R.id.supervisor_item);
            mItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "clicked on " + mSupervisor.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public SupervisorRecyclerViewAdapter(List<Supervisor> supervisors) {
        mSupervisors = supervisors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisor_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set the correct data in the list
        holder.mSupervisor = mSupervisors.get(position);
        holder.mName.setText(mSupervisors.get(position).getName());
        holder.mLicence.setText(mSupervisors.get(position).getLicenceNo());

    }

    public void updateList(List<Supervisor> supervisors) {
        mSupervisors = supervisors;
        notifyDataSetChanged();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSupervisors.size();
    }
}