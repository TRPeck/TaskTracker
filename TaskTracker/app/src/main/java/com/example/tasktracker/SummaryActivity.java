package com.example.tasktracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TaskDBHelper dbHelper;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        recyclerView = findViewById(R.id.taskSummaryList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        dbHelper = new TaskDBHelper(getApplicationContext());
        // populate tasks ArrayList with Task Summaries w/ total time
        tasks = (ArrayList<Task>) dbHelper.getTaskSummaries();

        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.summary_page);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.tasks_page:
                        intent = new Intent(SummaryActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.history_page:
                        intent = new Intent(SummaryActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        if(tasks != null){
            adapter = new TaskSummaryAdapter(tasks);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }
}