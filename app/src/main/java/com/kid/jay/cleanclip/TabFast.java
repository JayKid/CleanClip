package com.kid.jay.cleanclip;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class TabFast extends Activity {

    SharedItemsStore store;

    // Currently displayed item
    String cleanUrl = "";
    Boolean needsPersisting = false;
    WebInfo resolvedContents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fast);

        store = SharedItemsStore.getInstance();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemAt(0) != null) {
            String clipboardURL = String.valueOf(clipData.getItemAt(0).getText());
            cleanUrl = stripQueryParameters(clipboardURL);

            if (!cleanUrl.isEmpty()) {
                startUnfurling(cleanUrl);

                TextView clipboardLinkLabel = findViewById(R.id.clipboardLinkLabel);
                clipboardLinkLabel.setText(cleanUrl);
            }
        }

        bindFloatingButtonAction();
    }

    private String stripQueryParameters(String urlWithPotentialQueryParams) {
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
                    store.addSharedItem(cleanUrl, resolvedContents == null, resolvedContents);
                    needsPersisting = true;
                    startShareUrl(cleanUrl);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (needsPersisting) {
            store.persistSharedItems();
            needsPersisting = false;
        }
        super.onPause();
    }

    private void updateSharedItem(WebInfo resolvedContents) {
        this.resolvedContents = resolvedContents;
    }

    private void startUnfurling(String url) {

        TextCrawler textCrawler = new TextCrawler();

        LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
            @Override
            public void onPre() {
                // Might have to do sth here... (?)
            }

            @Override
            public void onPos(SourceContent sourceContent, boolean b) {

                WebInfo resolvedContents = extractWebsiteContents(sourceContent);
                updateSharedItem(resolvedContents);
                setPreviewFromExtractedContent(resolvedContents);
            }
        };

        textCrawler.makePreview(linkPreviewCallback, url);
    }

    private void setPreviewFromExtractedContent(WebInfo webInfo) {

        ImageView linkImagePreview = findViewById(R.id.linkImagePreview);
        TextView linkTitlePreview = findViewById(R.id.linkTitlePreview);
        TextView linkDescriptionPreview = findViewById(R.id.linkDescriptionPreview);

        linkTitlePreview.setText(webInfo.getTitle());
        linkDescriptionPreview.setText(webInfo.getDescription());
        Picasso.with(getApplicationContext()).load(webInfo.getMainImageURL()).into(linkImagePreview);
    }

    private WebInfo extractWebsiteContents(SourceContent sourceContent) {

        String title = "";
        String description = "";
        String mainImageURL = "http://imageog.flaticon.com/icons/png/512/36/36601.png";

        if (sourceContent.getTitle() != null) {
            title = sourceContent.getTitle();
        }
        if (sourceContent.getDescription() != null) {
            title = sourceContent.getDescription();
        }

        List<String> images = sourceContent.getImages();
        if (images != null && !images.isEmpty()) {
            String firstImageURL = images.get(0);
            if (firstImageURL != null) {
                mainImageURL = firstImageURL;
            }
        }

        return new WebInfo(title, description, mainImageURL);
    }
}
