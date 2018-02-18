package com.kid.jay.cleanclip;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SharedItemsStore {

    private static SharedItemsStore instance = new SharedItemsStore();
    private List<SharedItem> sharedItems = new ArrayList<>();
    private Storage storage;

    public void initialize(Context context) {
        storage = new Storage(context);
        sharedItems = storage.fetchSharedItemsFromFileSystem();
    }

    public static SharedItemsStore getInstance() {
        return instance;
    }

    public SharedItem getSharedItemByURL(String sharedItemURL) {
        List<SharedItem> matched = sharedItems.stream()
                .filter(item -> sharedItemURL.equals(item.getUrl()) )
                .collect(Collectors.toList());

        if (matched.size() == 1) {
            return matched.get(0);
        }
        return null;
    }

    public List<SharedItem> getSharedItems() {
        return sharedItems;
    }

    public void addSharedItem(String sharedItemURL, boolean isResolved, WebInfo resolvedContents) {
        List<SharedItem> matched = sharedItems.stream()
                .filter(item -> sharedItemURL.equals(item.getUrl()) )
                .collect(Collectors.toList());

        // Don't add duplicates :D
        if (matched.size() == 0) {
            sharedItems.add(new SharedItem(sharedItemURL, isResolved, resolvedContents));
        }

        // If it was already in, update it
        else if (matched.size() == 1) {
            SharedItem oldItem = matched.get(0);
            int listPosition = sharedItems.indexOf(oldItem);
            SharedItem updatedItem = new SharedItem(oldItem.getUrl(), true, resolvedContents);
            sharedItems.set(listPosition, updatedItem);
        }
    }

    public void persistSharedItems() {
        storage.save(sharedItems);
    }
}