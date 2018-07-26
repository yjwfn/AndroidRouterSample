package im.xingzhe.lib.router.action.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

import im.xingzhe.lib.router.action.DestinationAction;
import im.xingzhe.lib.router.action.UriDestinationActionBuilder;

public class DestinationActionBuilderImpl implements UriDestinationActionBuilder {

    int requestCode = -1;
    int flags = 0;
    boolean uriOnly;


    final Bundle arguments = new Bundle();
    final Uri.Builder uriBuilder = new Uri.Builder()
            .scheme("xingzhe")
            .authority("imxingzhe.com");


    WeakReference<Context> contextRef;




    @Override
    public UriDestinationActionBuilder requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    @Override
    public UriDestinationActionBuilder addFlags(int flag) {
        this.flags |= flag;
        return this;
    }

    @Override
    public UriDestinationActionBuilder uriOnly(boolean uriOnly) {
        this.uriOnly = uriOnly;
        return this;
    }

    @Override
    public UriDestinationActionBuilder authority(String authority) {
        uriBuilder.authority(authority);
        return this;
    }

    @Override
    public UriDestinationActionBuilder scheme(String scheme) {
        uriBuilder.scheme(scheme);
        return this;
    }

    @Override
    public UriDestinationActionBuilder path(String path) {
        uriBuilder.path(path);
        return this;
    }

    @Override
    public UriDestinationActionBuilder context(Context context) {
        contextRef = new WeakReference<>(context);
        return this;
    }

    @Override
    public UriDestinationActionBuilder put(String key, int value) {
        arguments.putInt(key, value);
        return this;
    }

    @Override
    public UriDestinationActionBuilder put(String key, long value) {
        arguments.putLong(key, value);
        return this;
    }

    @Override
    public UriDestinationActionBuilder put(String key, Parcelable parcelable) {
        arguments.putParcelable(key, parcelable);
        return this;
    }

    @Override
    public UriDestinationActionBuilder put(Bundle bundle) {
        if (bundle == null) {
            return this;
        }

        this.arguments.putAll(bundle);
        return this;
    }

    @Override
    public DestinationAction build() {
        return new DestinationActionImpl(this);
    }
}
