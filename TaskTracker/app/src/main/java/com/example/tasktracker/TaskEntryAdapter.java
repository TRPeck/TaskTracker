package com.example.tasktracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskEntryAdapter extends RecyclerView.Adapter<TaskEntryAdapter.EntryViewHolder> {

    private ArrayList<Task> tasks;

    public static class EntryViewHolder extends RecyclerView.ViewHolder {

        public TextView taskName;
        public TextView taskDate;
        public TextView entryTime;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_entry_name);
            taskDate = itemView.findViewById(R.id.taskDate);
            entryTime = itemView.findViewById(R.id.entryTime);
        }
    }

    public TaskEntryAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_card, parent, false);
        TaskEntryAdapter.EntryViewHolder entryViewHolder = new TaskEntryAdapter.EntryViewHolder(view);
        return entryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskName.setText(task.getName());
        holder.taskDate.setText(task.getCreated_at());
        holder.entryTime.setText(task.getTimer());
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


}
