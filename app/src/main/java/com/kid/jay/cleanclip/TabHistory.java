package com.kid.jay.cleanclip;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabHistory extends Activity implements AdapterView.OnItemClickListener {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    SharedItemsStore store;
    List<String> placeholderContents = Arrays.asList("You", "Never", "Shared", "Anything");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_history);

        store = SharedItemsStore.getInstance();
        listView = findViewById(R.id.historyListView);

        List<String> sharedItems = store.getSharedItems().stream().map(item -> item.getUrl()).collect(Collectors.toList());
        if (sharedItems.isEmpty()) {
            sharedItems = placeholderContents;
        }
        arrayAdapter = new ArrayAdapter<>(this, R.layout.history_list_item, sharedItems);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
