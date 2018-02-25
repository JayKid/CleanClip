package com.kid.jay.cleanclip;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends ActivityGroup {

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup(this.getLocalActivityManager());

        SharedItemsStore store = SharedItemsStore.getInstance();
        if (!SharedItemsStore.isInitialized()) {
            store.initialize(this);
        }

        TabHost.TabSpec spec = tabHost.newTabSpec("TabFast");
        spec.setContent(new Intent(this,TabFast.class));
        spec.setIndicator("Clipboard contents");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("TabHistory");
        spec.setContent(new Intent(this,TabHistory.class));
        spec.setIndicator("History");
        tabHost.addTab(spec);
    }
}