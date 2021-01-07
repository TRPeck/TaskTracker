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

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TaskDBHelper dbHelper;
    private ArrayList<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.taskEntryList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        dbHelper = new TaskDBHelper(getApplicationContext());
        // populate tasks ArrayList with Task Entries w/ dates and times
        tasks = (ArrayList<Task>) dbHelper.getAllTaskEntries();

        BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.history_page);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.tasks_page:
                        intent = new Intent(HistoryActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.summary_page:
                        intent = new Intent(HistoryActivity.this, SummaryActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        if(tasks != null){
            adapter = new TaskEntryAdapter(tasks);
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