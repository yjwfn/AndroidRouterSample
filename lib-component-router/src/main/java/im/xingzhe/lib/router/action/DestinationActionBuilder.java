package im.xingzhe.lib.router.action;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

public interface DestinationActionBuilder<Builder> {

    Builder context(Context context);

    Builder requestCode(int identifier);

    Builder addFlags(int flag);

    Builder put(String key, int value);

    Builder put(String key, long value);

    Builder put(String key, Parcelable parcelable);

    Builder put(Bundle arguments);

    DestinationAction build();
}
