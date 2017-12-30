package com.kid.jay.cleanclip;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

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
                    startShareUrl(cleanUrl);
                }
            }
        });
    }

}
