package im.xingzhe.lib.router.destination.impl;

import android.net.Uri;

import java.util.List;

public class UriDestinationDefinition extends DestinationDefinition {

    private final Uri uri;

    public UriDestinationDefinition(String name, Class<?> destination, List<DestinationArgumentDefinition> inArgumentDefinitions, List<DestinationArgumentDefinition> outArgumentDefinitions, Uri uri) {
        super(name, destination, inArgumentDefinitions, outArgumentDefinitions);
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
