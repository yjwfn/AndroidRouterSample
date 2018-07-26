package im.xingzhe.lib.router.action;

import android.content.Context;
import android.os.Bundle;

public interface DestinationAction {

    Context getContext();

    int getFlags();

    int getRequestCode();

    boolean getUriOnly();

    Bundle getArguments();

}
