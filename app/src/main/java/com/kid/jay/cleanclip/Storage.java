package com.kid.jay.cleanclip;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    Context context;

    private String FILENAME = "history.json";

    private String ITEM_URL = "url";
    private String ITEM_IS_RESOLVED = "isResolved";
    private String ITEM_RESOLVED_PROPS = "resolvedProperties";

    private String ITEM_PROPS_TITLE = "title";
    private String ITEM_PROPS_DESCRIPTION = "description";
    private String ITEM_PROPS_MAIN_IMAGE_URL = "mainImageURL";

    public Storage(Context context) {
        this.context = context;
    }

    public void save(List<SharedItem> items) {

        try {
            File file = new File(context.getFilesDir(), FILENAME);
            FileWriter fileWriter = new FileWriter(file);
            JsonWriter writer = new JsonWriter(fileWriter);
            writer.setIndent("  ");
            writer.beginArray();

            for (SharedItem item : items) {
                writer.beginObject();
                writer.name(ITEM_URL).value(item.getUrl());
                writer.name(ITEM_IS_RESOLVED).value(item.isResolved());

                if (item.isResolved()) {
                    writer.name(ITEM_RESOLVED_PROPS);
                    writer.beginObject();
                    writer.name(ITEM_PROPS_TITLE).value(item.getResolvedContents().getTitle());
                    writer.name(ITEM_PROPS_DESCRIPTION).value(item.getResolvedContents().getDescription());
                    writer.name(ITEM_PROPS_MAIN_IMAGE_URL).value(item.getResolvedContents().getMainImageURL());
                    writer.endObject();
                }
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SharedItem> fetchSharedItemsFromFileSystem() {
        List<SharedItem> sharedItems = new ArrayList<>();

        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                JsonReader reader = new JsonReader(fileReader);

                reader.beginArray();
                while (reader.hasNext()) {
                    sharedItems.add(readSharedItem(reader));
                }
                reader.endArray();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sharedItems;
    }

    private SharedItem readSharedItem(JsonReader reader) throws IOException {
        // Primary properties
        String url = null;
        boolean isResolved = false;

        // Resolved contents if available
        String title  = null;
        String description  = null;
        String mainImageURL = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals(ITEM_URL)) {
                url = reader.nextString();
            } else if (name.equals(ITEM_IS_RESOLVED)) {
                isResolved = reader.nextBoolean();
            } else if (name.equals(ITEM_RESOLVED_PROPS)) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String propsName = reader.nextName();
                    if (propsName.equals(ITEM_PROPS_TITLE)) {
                        title = reader.nextString();
                    } else if (propsName.equals(ITEM_PROPS_DESCRIPTION)) {
                        description = reader.nextString();
                    } else if (propsName.equals(ITEM_PROPS_MAIN_IMAGE_URL)) {
                        mainImageURL = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new SharedItem(url, isResolved, title, description, mainImageURL);
    }
}
