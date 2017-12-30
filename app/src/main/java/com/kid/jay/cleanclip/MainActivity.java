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

        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("TabFast");
        spec.setContent(new Intent(this,TabFast.class));
        spec.setIndicator("Clipboard contents");
        tabHost.addTab(spec);

        //Tab 2
        spec = tabHost.newTabSpec("TabHistory");
        spec.setContent(R.id.tab2);
        spec.setIndicator("History");
        tabHost.addTab(spec);
    }
}