package com.kid.jay.cleanclip;

public class SharedItem {

    public String getUrl() {
        return url;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public WebInfo getResolvedContents() {
        return resolvedContents;
    }

    private String url;
    private boolean isResolved = false;
    private WebInfo resolvedContents;

    public SharedItem(String url, boolean isResolved, String title, String description, String mainImageUrl) {
        this.url = url;
        this.isResolved = isResolved;
        this.resolvedContents = new WebInfo(title, description, mainImageUrl);
    }

    public SharedItem(String url, boolean isResolved, WebInfo resolvedContents) {
        this.url = url;
        this.isResolved = isResolved;
        this.resolvedContents = resolvedContents;
    }
}
