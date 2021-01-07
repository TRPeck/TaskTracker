package com.example.tasktracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskSummaryAdapter extends RecyclerView.Adapter<TaskSummaryAdapter.SummaryViewHolder> {

    public ArrayList<Task> tasks;

    public static class SummaryViewHolder extends RecyclerView.ViewHolder {

        public TextView taskName;
        public TextView taskTime;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.task_summary_name);
            taskTime = itemView.findViewById(R.id.summaryTime);
        }
    }

    public TaskSummaryAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_card, parent, false);
        TaskSummaryAdapter.SummaryViewHolder summaryViewHolder = new TaskSummaryAdapter.SummaryViewHolder(view);
        return summaryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskName.setText(task.getName());
        holder.taskTime.setText(task.getTimer());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
