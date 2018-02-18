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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TabFast extends Activity {

    SharedItemsStore store;

    RequestQueue queue;

    // Temporary sharedItem currently being displayed
    String sharedItemURL = "";
    Boolean needsPersisting = false;
    WebInfo resolvedContents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_fast);

        store = SharedItemsStore.getInstance();
        queue = Volley.newRequestQueue(this);
        bindFloatingButtonAction();
    }

    @Override
    protected void onResume() {
        super.onResume();

        resetTemporarySharedItemState();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemAt(0) != null) {
            String clipboardURL = String.valueOf(clipData.getItemAt(0).getText());
            sharedItemURL = URLTools.stripQueryParameters(clipboardURL);

            if (!sharedItemURL.isEmpty()) {

                SharedItem sharedItemFromStorage = store.getSharedItemByURL(sharedItemURL);
                if (sharedItemFromStorage != null) {
                    if (sharedItemFromStorage.isResolved()) {
                        resolvedContents = sharedItemFromStorage.getResolvedContents();
                        setPreviewFromExtractedContent(resolvedContents);
                    }
                    else {
                        startUnfurling(sharedItemURL);
                    }
                }
                else {
                    startUnfurling(sharedItemURL);
                }

                TextView clipboardLinkLabel = findViewById(R.id.clipboardLinkLabel);
                clipboardLinkLabel.setText(sharedItemURL);
            }
        }
    }

    private void resetTemporarySharedItemState() {
        sharedItemURL = "";
        resolvedContents = null;
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
                if (!sharedItemURL.isEmpty()) {
                    store.addSharedItem(sharedItemURL, resolvedContents == null, resolvedContents);
                    needsPersisting = true;
                    startShareUrl(sharedItemURL);
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

        if (URLTools.isTwitter(url)) {
            startAPIUnfurling(url);
        }
        else {
            startLocalDeviceUnfurling(url);
        }
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

    private void startLocalDeviceUnfurling(String url) {
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

    // API Interaction <- Smells like it wants its own file :(

    private void startAPIUnfurling(String sourceUrl) {
        String API_URL = "https://api.linkpreview.net";
        String URL_PARAM_KEY = "q";
        String APIKEY_PARAM_KEY = "key";

        String KEY = "YOUR_KEY_HERE"; // REPLACE WITH REAL KEY :D

        // Instantiate the RequestQueue.
        String url = API_URL + '?' + APIKEY_PARAM_KEY + "=" + KEY + "&" + URL_PARAM_KEY + "=" + sourceUrl;

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        WebInfo resolvedContents = extractFromAPIResponse(response);
                        updateSharedItem(resolvedContents);
                        setPreviewFromExtractedContent(resolvedContents);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), "LinkPreview API went kaputt", duration);
                toast.show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private WebInfo extractFromAPIResponse(JSONObject response) {
        Map<String, String> resolvedContents = new HashMap<>();

        Iterator it = response.keys();
        while(it.hasNext()) {
            String name = (String) it.next();
            if (name != null && !name.isEmpty()) {

                String value = null;
                try {
                    value = response.getString(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resolvedContents.put(name, value);
            }
        }
        String title = resolvedContents.get("title");
        if (title == null) {
            title = "";
        }
        String description = resolvedContents.get("description");
        if (description == null) {
            description = "";
        }
        String mainImageUrl = resolvedContents.get("image");
        if (mainImageUrl == null) {
            mainImageUrl = "http://imageog.flaticon.com/icons/png/512/36/36601.png";
        }
        return new WebInfo(title, description, mainImageUrl);
    }
}
