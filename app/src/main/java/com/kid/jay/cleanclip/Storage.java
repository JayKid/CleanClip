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

    public Storage(Context context) {
        this.context = context;
    }

    public void save(List<String> urls) {

        try {
            File file = new File(context.getFilesDir(), "history.json");
            FileWriter fileWriter = new FileWriter(file);
            JsonWriter writer = new JsonWriter(fileWriter);
            writer.setIndent("  ");
            writer.beginArray();

            for (String url : urls) {
                writer.beginObject();
                writer.name("url").value(url);
                writer.endObject();
            }
            writer.endArray();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> fetchSharedItemsFromFileSystem() {
        try {
            File file = new File(context.getFilesDir(),"history.json");
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
            return urls;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
