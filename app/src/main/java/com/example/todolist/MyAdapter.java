package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Model> dataholder;
    private OnTaskCompletedListener listener;

    public MyAdapter(ArrayList<Model> dataholder, OnTaskCompletedListener listener) {
        this.dataholder = dataholder;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reminder_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Model currentItem = dataholder.get(position);

        holder.mTitle.setText(currentItem.getTitle());
        holder.mDescription.setText(currentItem.getDescription());
        holder.taskCheckBox.setChecked(currentItem.isCompleted());

        holder.taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentItem.setCompleted(isChecked);

                if (isChecked) {
                    listener.onTaskCompleted(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mDescription;
        CheckBox taskCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.txtTitle);
            mDescription = itemView.findViewById(R.id.txtDesc);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
        }
    }

    interface OnTaskCompletedListener {
        void onTaskCompleted(Model completedTask);
    }
}

