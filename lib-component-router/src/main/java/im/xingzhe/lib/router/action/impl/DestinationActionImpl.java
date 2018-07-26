package im.xingzhe.lib.router.action.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

import im.xingzhe.lib.router.action.UriDestinationAction;

public class DestinationActionImpl implements UriDestinationAction {

    private final int requestCode;
    private final int flags;
    private final boolean uriOnly;
    private final Uri uri;
    private final Bundle arguments;
    private final WeakReference<Context> contextRef;


    DestinationActionImpl(DestinationActionBuilderImpl builder) {
        this.requestCode = builder.requestCode;
        this.uri = builder.uriBuilder.build();
        this.flags = builder.flags;
        this.uriOnly = builder.uriOnly;
        this.arguments = builder.arguments;
        this.contextRef = builder.contextRef;
    }

    @Override
    public boolean getUriOnly() {
        return uriOnly;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public int getRequestCode() {
        return requestCode;
    }

    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    public Context getContext() {
        return contextRef != null ? contextRef.get() : null;
    }

    @Override
    public Bundle getArguments() {
        return arguments;
    }
}
