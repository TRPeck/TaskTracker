package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static RecyclerView.Adapter adapter;
    public static RecyclerView.LayoutManager layoutManager;
    private TaskDBHelper dbHelper;
    EditText txt;
    private ArrayList<Task> tasks;
    private String taskName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.taskList);
        recyclerView.setHasFixedSize(true);
        // Create layout manager to display items vertically
        layoutManager = new LinearLayoutManager(this);
        dbHelper = new TaskDBHelper(getApplicationContext());
        // Load tasks ArrayList with Tasks from Tasks table for displaying in cards
        tasks = (ArrayList<Task>) dbHelper.getAllTasks();
        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.tasks_page);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.history_page:
                        intent = new Intent(MainActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.summary_page:
                        intent = new Intent(MainActivity.this, SummaryActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        // if tasks exist create and set adapter to display them
        if(tasks != null){
            adapter = new TaskAdapter(tasks);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // method replaces default transition animation with no animation (0)
        overridePendingTransition(0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }

    public void newTask(View view) {
        AlertDialog.Builder addNewTask = new AlertDialog.Builder(this);
        // EditText for user to enter name of task
        final EditText taskName = new EditText(MainActivity.this);

        addNewTask.setTitle("Add New Task");
        addNewTask.setView(taskName);
        // Layout for items in AlertDialog
        LinearLayout addNewTaskLayout = new LinearLayout(this);
        addNewTaskLayout.setOrientation(LinearLayout.VERTICAL);
        addNewTaskLayout.addView(taskName); // add user input bar to layout
        addNewTask.setView(addNewTaskLayout); // set AlertDialog to display layout

        addNewTask.setPositiveButton("New Task", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                txt = taskName;
                MainActivity.this.taskName = collectInput(); // analyze input (txt) in this method
                Task task = new Task(MainActivity.this.taskName);
                long task_id = dbHelper.addTask(task);
                recreate();
            }
        });

        addNewTask.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        addNewTask.show();
        //

    }

    // method to get name of task from AlertDialog
    public String collectInput(){
        // convert edit text to string
        String getInput = txt.getText().toString();

        // ensure that user input bar is not empty
        if (getInput ==null || getInput.trim().equals("")){
            Toast.makeText(getBaseContext(), "Please add a task name", Toast.LENGTH_LONG).show();
        }
        // return name of task as String
        return getInput;
    }
}