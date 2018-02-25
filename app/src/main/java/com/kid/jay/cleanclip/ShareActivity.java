package com.kid.jay.cleanclip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class ShareActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> placeholderContents = Arrays.asList("You", "Never", "Shared", "Anything");
    private SharedItemsStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        store = SharedItemsStore.getInstance();
        if (!SharedItemsStore.isInitialized()) {
            store.initialize(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get the intent that started this activity
        Intent intent = getIntent();

        List<String> links = new ArrayList<>();

        // Figure out what to do based on the intent type
        if (intent.getType().indexOf("text/plain") != -1) {
            String contents = intent.getStringExtra(Intent.EXTRA_TEXT);
            links = extractLinksFromText(contents);
        } else if (intent.getType().equals("text/html")) {
            // Handle intents with text ...
//            String contents = intent.getDataString();
        }
        else {
//            String contents = intent.getDataString();
        }

        listView = findViewById(R.id.shareListView);

        List<String> sharedItems = links;
        if (sharedItems.isEmpty()) {
            sharedItems = placeholderContents;
        }
        arrayAdapter = new ArrayAdapter<>(this, R.layout.share_list_item, sharedItems);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    private void startShareUrl(String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private List<String> extractLinksFromText(String text) {
        List<String> links = new ArrayList<>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(URLTools.stripQueryParameters(url));
        }

        return links;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long longId) {
        String link = (String) parent.getAdapter().getItem(position);
        store.addSharedItem(link, false, null);
        startShareUrl(link);
    }
}
