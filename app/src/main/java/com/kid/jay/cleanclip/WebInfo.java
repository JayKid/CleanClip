package com.kid.jay.cleanclip;

public class WebInfo {
    private String title;
    private String description;
    private String mainImageURL;

    public WebInfo(String title, String description, String mainImageURL) {
        this.title = title;
        this.description = description;
        this.mainImageURL = mainImageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMainImageURL() {
        return mainImageURL;
    }
}
