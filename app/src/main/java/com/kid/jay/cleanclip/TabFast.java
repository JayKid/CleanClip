package com.kid.jay.cleanclip;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class TabFast extends Activity {

    String cleanUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fast);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemAt(0) != null) {
            String clipboardURL = String.valueOf(clipData.getItemAt(0).getText());
            cleanUrl = stripQueryParameters(clipboardURL);

            if (!cleanUrl.isEmpty()) {
                TextView clipboardLinkLabel = findViewById(R.id.clipboardLinkLabel);
                clipboardLinkLabel.setText(cleanUrl);
            }
        }

        bindFloatingButtonAction();
    }

    private String stripQueryParameters (String urlWithPotentialQueryParams) {
        try {
            URI sourceUri = new URI(urlWithPotentialQueryParams);
            return new URI(sourceUri.getScheme(),
                    sourceUri.getAuthority(),
                    sourceUri.getPath(),
                    null, // Ignore the query part of the input url
                    sourceUri.getFragment()).toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void startShareUrl(String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, url);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void bindFloatingButtonAction() {
        FloatingActionButton shareButton = findViewById(R.id.fastShareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!cleanUrl.isEmpty()) {
                    writeToJSON();
                    readFromFile();
                    startShareUrl(cleanUrl);
                }
            }
        });
    }

    private void writeToJSON() {

        try {
            File file = new File(getFilesDir(), "history.json");
            FileWriter fileWriter = new FileWriter(file);
            JsonWriter writer = new JsonWriter(fileWriter);
            writer.setIndent("  ");
            writer.beginArray();

            for (int i = 0; i < 1; ++i) {
                writer.beginObject();
                writer.name("url").value(cleanUrl);
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile() {
        try {
            File file = new File(getFilesDir(),"history.json");
            FileReader fileReader = new FileReader(file);
            JsonReader reader = new JsonReader(fileReader);
            List<String> urls = new ArrayList<>();

            reader.beginArray();
            while (reader.hasNext()) {
                String url = null;
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("url")) {
                        url = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();

                if (url != null) {
                    urls.add(url);
                }
            }
            reader.endArray();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
