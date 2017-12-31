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

public class TabHistory extends Activity implements AdapterView.OnItemClickListener {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    Storage storage;
    List<String> placeholderContents = Arrays.asList("You", "Never", "Shared", "Anything");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_history);

        storage = new Storage(this);
        listView = findViewById(R.id.historyListView);

        List<String> sharedItems = storage.fetchSharedItemsFromFileSystem();
        if (sharedItems == null) {
            sharedItems = placeholderContents;
        }
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sharedItems);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView tv = (TextView)view;
        Toast.makeText(getApplicationContext(),"You Clicked "+tv.getText()+" position "+i,Toast.LENGTH_SHORT).show();
    }
}
