package com.example.tasktracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // ArrayList for tasks which we will use to populate cards
    private static ArrayList<Task> tasks;
    private static String textTimer;

    public static class TimerText {
        String timerText;

        public TimerText(){
            timerText = " ";
        }

        public TimerText(String text){
            timerText = text;
        }

        public synchronized String getTimerText(){
            return timerText;
        }

        public synchronized void setTimerText(String timerText){
            this.timerText = timerText;
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        // variables to represent layout elements for tasks
        public TextView taskName;
        public ImageView taskColor;
        public ImageView btnPlay;
        public ImageView btnPause;
        public ImageView btnStop;
        public TextView timer;

        public static TimerText timerText;

        // represent and manage time for running Tasks
        public long startTime;
        public long pauseTime;
        public boolean started;

        public Handler timerHandler;
        public Runnable timerRunnable;

        public TaskDBHelper helper;

        // constants for intent extras that will be passed for notifications
        public static final String TASK_NAME = "taskName";
        public static final String TIMER_EXTRA = "timerExtra";

        // RecyclerView components for representing RecyclerView in MainActivity
        public RecyclerView recyclerView;
        public RecyclerView.LayoutManager manager;
        private static RecyclerView.ViewHolder holder;
        public RecyclerView.Adapter adapter;


        private static boolean first = true; // for checking if Task is first to be activated
        private static int pos = 0; // the position of an item in RecyclerView
        public static final String TAG = "RecyclerView"; // tag for Logging

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.taskList);
            cardView = itemView.findViewById(R.id.card_view);
            taskName = itemView.findViewById(R.id.card_task_name);
            taskColor = itemView.findViewById(R.id.task_color);
            timer = itemView.findViewById(R.id.timer);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnPause = itemView.findViewById(R.id.btn_pause);
            btnStop = itemView.findViewById(R.id.btn_stop);
            startTime = 0;
            pauseTime = 0;
            started = false;
            Task task = new Task(); // new task used to represent task entry information
            timerText = new TimerText();
            helper = new TaskDBHelper(itemView.getContext());
            recyclerView = MainActivity.recyclerView;
            manager = MainActivity.layoutManager;
            adapter = MainActivity.adapter;
            //timerText = new TimerText("Test... Again");

            // Handler and Runnable to implement timers running on separate thread... I think
            // TODO: 1/2/2021 research these and get a good understanding of how they work
            timerHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    String time = msg.getData().getString("timer");
                    timer.setText(time);
                }
            };

            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    // method used to calculate and display the current time of the timer when running

                    // millies will give the difference between the current time and when the timer was started
                    // (the amount of time it has run) plus what value the timer had when it was paused
                    // (as pausing and restarting will restart the timer)
                    long millis = (System.currentTimeMillis() - startTime) + pauseTime;
                    // because the System gives time in millis, convert to seconds and minutes
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;

                    timer.setVisibility(View.VISIBLE); // show the timer
                    timerText.setTimerText(String.format("%d:%02d", minutes, seconds));
                    timer.setText(String.format("%d:%02d", minutes, seconds));
                    textTimer = timerText.getTimerText();
                    tasks.get(pos).setTimer(timerText.getTimerText());
                    helper.updateTaskTimer(tasks.get(pos));

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    String time = String.format("%d:%02d", minutes, seconds);
                    bundle.putString("timer", time);
                    message.setData(bundle);
                    timerHandler.sendMessage(message);

                    // start service for showing notification for timer + send timer information
                    // ContextCompat.startForegroundService rather than startService as it will
                    // select right method for all versions of Android
                    ContextCompat.startForegroundService(itemView.getContext(), startTimerService());

                    // post all this to the message queue
                    timerHandler.postDelayed(this, 500);
                }
            };

            // this onClickListener for starting timer on clicking Card and stopping timer of previous Card if running
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // need to check if first as holder variable will be null
                    if(holder == null){
                        // get adapter position of Card and set to variable
                        pos = (int)v.getTag();
                        // set corresponding task to active for checking next ones
                        tasks.get(pos).setActive(1);
                        helper.updateTaskActivity(tasks.get(pos));
                        // get ViewHolder of Card and set to variable accessing when next card is clicked
                        holder = recyclerView.findViewHolderForAdapterPosition(pos);
                        btnPlay.performClick();
                    }
                    // if current task is not running so we don't restart it
                    else if(tasks.get((int)v.getTag()).isActive() != 1 && tasks.get(pos).isActive() == 1){
                        holder.itemView.findViewById(R.id.btn_stop).performClick();
                        tasks.get(pos).setActive(0);
                        helper.updateTaskActivity(tasks.get(pos));
                        btnPlay.performClick();
                        pos = (int)v.getTag();
                        tasks.get(pos).setActive(1);
                        helper.updateTaskActivity(tasks.get(pos));
                        // get ViewHolder of Card and set to variable accessing when next card is clicked
                        holder = recyclerView.findViewHolderForAdapterPosition(pos);
                    }
                    else if(tasks.get((int)v.getTag()).isActive() != 1 && tasks.get(pos).isActive() != 1){
                        btnPlay.performClick();
                        pos = (int)v.getTag();
                        tasks.get(pos).setActive(1);
                        helper.updateTaskActivity(tasks.get(pos));
                        holder = recyclerView.findViewHolderForAdapterPosition(pos);
                    }
                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if timer hasn't been started yet, i.e., first time this is clicked
                    if(started != true){
                        task.setCreated_at(task.getCurrentDateTime());
                        started = true;
                    }
                    tasks.get(pos).setActive(1);
                    helper.updateTaskActivity(tasks.get(pos));
                    startTime = System.currentTimeMillis(); // set startTime to current time
                    timerHandler.postDelayed(timerRunnable, 0); // post
                    // set correct ImageView button visibility
                    btnPlay.setVisibility(View.GONE);
                    btnPause.setVisibility(View.VISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                }
            });
            btnPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timerHandler.removeCallbacks(timerRunnable); // stop the timer running
                    btnPause.setVisibility(View.GONE);
                    btnPlay.setVisibility(View.VISIBLE);
                    // set timer value at pause to a variable to give when restarting timer
                    pauseTime = pauseTime + (System.currentTimeMillis() - startTime);
                }
            });
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tasks.get(pos).setActive(0);
                    helper.updateTaskActivity(tasks.get(pos));
                    tasks.get(pos).setTimer("");
                    helper.updateTaskTimer(tasks.get(pos));
                    timerHandler.removeCallbacks(timerRunnable); // stop timer
                    pauseTime = 0; // reset the amount of time at pause to zero

                    // add values to Task Entry and add to corresponding table
                    task.setName(taskName.getText().toString());
                    task.setDateTimeEnd(task.getCurrentDateTime());
                    task.setTimer(timer.getText().toString());
                    helper.addTaskTime(task);

                    btnStop.setVisibility(View.GONE);
                    timer.setVisibility(View.GONE);
                    if(btnPause.getVisibility() == View.VISIBLE){
                        btnPause.setVisibility(View.GONE);
                        btnPlay.setVisibility(View.VISIBLE);
                    }
                    // stop notification service
                    v.getContext().stopService(stopTimerService());
                }
            });
        }

        // used to pass task information to notification service as intent
        public Intent startTimerService(){
            String time = timer.getText().toString();
            String name = taskName.getText().toString();

            Intent serviceIntent = new Intent(itemView.getContext(), TimerService.class);
            serviceIntent.putExtra(TASK_NAME, name);
            serviceIntent.putExtra(TIMER_EXTRA, time);

            return serviceIntent;
        }
        public Intent stopTimerService(){
            Intent serviceIntent = new Intent(itemView.getContext(), TimerService.class);
            return serviceIntent;
        }
    }

    // constructor to pass ArrayList of tasks for binding to the ViewHolder
    public TaskAdapter(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_card, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // get task from ArrayList based on position
        Task task = tasks.get(position);

        if(task.isActive() == 1){
            holder.btnPlay.setVisibility(View.GONE);
            holder.btnPause.setVisibility(View.VISIBLE);
            holder.btnStop.setVisibility(View.VISIBLE);
            holder.timer.setVisibility(View.VISIBLE);
            holder.timerHandler.postDelayed(holder.timerRunnable, 0);
        }

        // set layout items to values taken from task
        holder.taskName.setText(task.getName());
        holder.cardView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


}
