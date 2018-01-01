package com.kid.jay.cleanclip;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class SharedItemsStore {

    private static SharedItemsStore instance = new SharedItemsStore();
    private List<String> sharedItems = new ArrayList<>();
    private Storage storage;

    public void initialize(Context context) {
        storage = new Storage(context);
    }

    public static SharedItemsStore getInstance() {
        return instance;
    }

    public List<String> getSharedItems() {
        if (!sharedItems.isEmpty()) {
            return sharedItems;
        }
        else {
            sharedItems = storage.fetchSharedItemsFromFileSystem();
            return sharedItems;
        }
    }

    public void addSharedItem(String sharedItem) {
        sharedItems.add(sharedItem);
    }

    public void persistSharedItems() {
        storage.save(sharedItems);
    }
}