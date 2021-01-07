package com.example.tasktracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// class to represent tasks for both Tasks and TaskEntries tables
public class Task implements Serializable {
    private int id;
    private String name;
    private String color;
    private String dateTimeEnd;
    private String timer;
    private String created_at;
    private int isActive;

    public Task(){

    }

    public Task(String name){
        this.name = name;
    }

    // constructor for use when colour picker added and Tasks have associated colours
    public Task(String name, String color){
        this.name = name;
        this.color = color;
    }

    // method to get current date/time info in SimpleDateFormat
    public String getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
        );
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(String dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    // used with getCurrentDateTime() when Tasks are first started
    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    // string to hold the running time(main activity) or the total time(summary activity) for task
    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    // TODO: 1/2/2021 find colour picker, integrate with main activity, associate with tasks
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // to check if the task's timer is running in the main activity
    public int isActive() {
        return isActive;
    }

    public void setActive(int isActive) {
        this.isActive = isActive;
    }
}
