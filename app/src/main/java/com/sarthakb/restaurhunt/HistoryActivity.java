package com.sarthakb.restaurhunt;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.history_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<FoodItem> items = (ArrayList<FoodItem>) getIntent().getSerializableExtra("History");
        StagViewAdapter adapter = new StagViewAdapter(this, items);
        recyclerView.setAdapter(adapter);

    }

}
