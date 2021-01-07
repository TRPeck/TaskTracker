package com.example.tasktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TASK_ENTRIES = "task_entries";

    public static final String ID = "id";
    public static final String TASK_NAME = "name";
    public static final String CREATED_AT = "created_at";

    public static final String DATE_TIME_START = "date_time_start";
    public static final String DATE_TIME_END = "date_time_end";
    // table column to hold Strings representing time spent on single session of Task
    public static final String TIMER = "timer";
    // TODO: 1/2/2021 table column to hold some kind of colour code
    public static final String COLOUR = "colour";

    public static final String IS_ACTIVE = "isActive";

    // simple table to hold basic task info (names and colours)
    public static final String CREATE_TABLE_TASKS = "CREATE TABLE " +
            TABLE_TASKS + "(" + ID + " INTEGER PRIMARY KEY," + TASK_NAME + " TEXT," +
            CREATED_AT + " DATETIME," + IS_ACTIVE + " INTEGER DEFAULT 0," + TIMER + " TEXT DEFAULT ''" + ")";

    // table to hold more complex info (dates/times) for History and Summary entries
    public static final String CREATE_TABLE_TASK_ENTRIES = "CREATE TABLE " +
            TABLE_TASK_ENTRIES + "(" + ID + " INTEGER PRIMARY KEY," + TASK_NAME + " TEXT," +
            DATE_TIME_START + " DATETIME," + DATE_TIME_END + " DATETIME," + TIMER + " TEXT" + ")";

    public TaskDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_TASK_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK_ENTRIES);
        onCreate(db);
    }

    // TODO: 1/2/2021 research SQLite, make these functions more efficient i.e. use ids

    public long addTask(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TASK_NAME, task.getName());
        cv.put(CREATED_AT, task.getCurrentDateTime());

        long task_id = db.insert(TABLE_TASKS, null, cv);
        return task_id;
    }

    // perhaps don't really need this method it seems
    public Task getTask(String name){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " +
                name + " = " + TASK_NAME;

        Cursor cursor = db.rawQuery(query, null);

        Task task = new Task();
        task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        task.setName(cursor.getString(cursor.getColumnIndex(TASK_NAME)));
        task.setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)));

        return task;
    }

    public void updateTaskActivity(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(IS_ACTIVE, task.isActive());

        db.update(TABLE_TASKS, cv, "name = ?" , new String[]{task.getName()});
    }

    public void updateTaskTimer(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TIMER, task.getTimer());

        db.update(TABLE_TASKS, cv, "name = ?" , new String[]{task.getName()});
    }

    // method for getting list of simple tasks from tasks table
    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<Task>();
        String query = "SELECT * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        // if query returns results
        if(cursor.moveToFirst()){
            // create task, add info from table, add to ArrayList
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(TASK_NAME)));
                task.setCreated_at(cursor.getString(cursor.getColumnIndex(CREATED_AT)));
                task.setActive(cursor.getInt(cursor.getColumnIndex(IS_ACTIVE)));
                task.setTimer(cursor.getString(cursor.getColumnIndex(TIMER)));
                tasks.add(task);
            } while(cursor.moveToNext()); // while more tasks left in query
        }
        return tasks;

    }

    // method for adding timers to task entries
    // TODO: 1/2/2021 create method for updating timers
    public long addTaskTime(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TASK_NAME, task.getName());
        cv.put(DATE_TIME_START, task.getCreated_at());
        cv.put(DATE_TIME_END, task.getDateTimeEnd());
        cv.put(TIMER, task.getTimer());

        long task_entry_id = db.insert(TABLE_TASK_ENTRIES, null, cv);
        return task_entry_id;
    }

    // method for getting list of more complex tasks from task entries table
    public List<Task> getAllTaskEntries(){
        List<Task> tasks = new ArrayList<Task>();
        String query = "SELECT * FROM " + TABLE_TASK_ENTRIES + " ORDER BY " + ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                task.setName(cursor.getString(cursor.getColumnIndex(TASK_NAME)));
                task.setCreated_at(cursor.getString(cursor.getColumnIndex(DATE_TIME_START)));
                task.setTimer(cursor.getString(cursor.getColumnIndex(TIMER)));
                tasks.add(task);
            } while(cursor.moveToNext());
        }
        return tasks;

    }

    // method for creating list of tasks + their total times
    public List<Task> getTaskSummaries() {

        // get the task names and timers for task entries, order by task name for summing related entries
        String query = "SELECT " + TASK_NAME + ", " + TIMER + " FROM " + TABLE_TASK_ENTRIES + " ORDER BY " + TASK_NAME + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<Task> taskSummaries = new ArrayList<>();
        // String for holding name of current task
        String current = "";
        // String array to hold minutes/seconds of timer after splitting by ':'
        String[] timer;
        // integer to hold total time spent on task (perhaps make long or something for safety)
        int totalTime = 0;
        if (cursor.moveToFirst()) {
            do {
                // if last item in query, must be considered for case of previous item being different task
                if (cursor.isLast()) {
                    // get task's timer String and split into minutes/seconds by delimiter
                    timer = cursor.getString(cursor.getColumnIndex(TIMER)).split(":");
                    // take String representation of minutes, parse int and multiply by 60 and add to totalTime as seconds
                    totalTime += Integer.parseInt(timer[0]) * 60;
                    // parse seconds from String and add to totalTime
                    totalTime += Integer.parseInt(timer[1]);
                    // new task to hold name and total time information
                    Task task = new Task();
                    // get minutes out of total time
                    int minutes = totalTime / 60;
                    // get seconds out of total time by getting reminder of minutes division
                    int seconds = totalTime % 60;
                    // create String with these to represent total time (conversion done automatically)
                    String minSec = minutes + ":" + seconds;
                    // get name of current task in query from cursor, set for new task
                    task.setName(cursor.getString(cursor.getColumnIndex(TASK_NAME)));
                    // set new String for total time to new task
                    task.setTimer(minSec);
                    taskSummaries.add(task);

                    // check first to set up values for potential postceding entries of same name
                } else if (cursor.isFirst()) {
                    current = cursor.getString(cursor.getColumnIndex(TASK_NAME));
                    timer = cursor.getString(cursor.getColumnIndex(TIMER)).split(":");
                    totalTime += Integer.parseInt(timer[0]) * 60;
                    totalTime += Integer.parseInt(timer[1]);

                    // if current task is not the same as previous and the running count of total time is not
                    // zero, current task must be different so set info and add previous task to ArrayList
                } else if (!cursor.getString(cursor.getColumnIndex(TASK_NAME)).equals(current) && totalTime != 0) {
                    Task task = new Task();
                    int minutes = totalTime / 60;
                    int seconds = totalTime % 60;
                    String minSec = minutes + ":" + seconds;
                    task.setName(current);
                    task.setTimer(minSec);
                    taskSummaries.add(task);
                    current = cursor.getString(cursor.getColumnIndex(TASK_NAME));
                    totalTime = 0;
                    timer = cursor.getString(cursor.getColumnIndex(TIMER)).split(":");
                    totalTime += Integer.parseInt(timer[0]) * 60;
                    totalTime += Integer.parseInt(timer[1]);

                    // if the current task is the same as the previous, just get and add its timer to total time
                } else if (cursor.getString(cursor.getColumnIndex(TASK_NAME)).equals(current)) {
                    timer = cursor.getString(cursor.getColumnIndex(TIMER)).split(":");
                    totalTime += Integer.parseInt(timer[0]) * 60;
                    totalTime += Integer.parseInt(timer[1]);
                } 
            } while (cursor.moveToNext());
        }
        return taskSummaries;
    }
}
