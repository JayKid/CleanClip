package com.kid.jay.cleanclip;

import java.net.URI;
import java.net.URISyntaxException;

public class URLTools {

    private static String TWITTER = "twitter";

    public static String stripQueryParameters(String urlWithPotentialQueryParams) {
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

    public static boolean isTwitter(String url) {
        try {
            URI sourceUri = new URI(url);
            return sourceUri.getHost().contains(TWITTER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
