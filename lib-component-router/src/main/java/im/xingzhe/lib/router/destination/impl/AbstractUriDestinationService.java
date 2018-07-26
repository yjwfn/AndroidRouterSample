package im.xingzhe.lib.router.destination.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

import im.xingzhe.lib.router.action.DestinationAction;
import im.xingzhe.lib.router.action.UriDestinationAction;
import im.xingzhe.lib.router.destination.DestinationService;

public abstract class AbstractUriDestinationService implements DestinationService {

    private final static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static boolean isDestinationDefinitionResolved;


    @Override
    public void start(DestinationAction destinationAction) {
        List<DestinationDefinition> destinationDefinitions = getDestinationDefinitions();
        resolveDestinationDefinition(destinationDefinitions);

        Context context = destinationAction.getContext();

        if (context == null) {
            throw new IllegalArgumentException("content == null");
        }

        PackageManager packageManager = context.getPackageManager();

        if (destinationAction instanceof UriDestinationAction) {
            Uri uri = ((UriDestinationAction) destinationAction).getUri();
            int index = matcher.match(uri);

            if (UriMatcher.NO_MATCH == index || index >= destinationDefinitions.size()) {
                throw new IllegalStateException("Not found destination for : " + uri);
            }

            DestinationDefinition destinationDefinition = destinationDefinitions.get(index);
            List<DestinationArgumentDefinition> destinationArgumentDefinitions = destinationDefinition.getInArgumentDefinitions();
            for (DestinationArgumentDefinition argumentDefinition : destinationArgumentDefinitions) {
                Bundle args = destinationAction.getArguments();
                if (argumentDefinition.isRequire() && !args.containsKey(argumentDefinition.getKey())) {
                    throw new IllegalArgumentException("No such key: " + argumentDefinition.getKey());
                }

            }


            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (packageManager.resolveActivity(intent, 0) == null) {
                if (destinationAction.getUriOnly()) {
                    throw new IllegalStateException("Not found activity for : " + uri);
                } else {
                    intent = new Intent(context, destinationDefinition.getDestination());

                    if (packageManager.resolveActivity(intent, 0) == null) {
                        throw new IllegalStateException("Not found activity for : " + uri);
                    }
                }
            }


            intent.addFlags(destinationAction.getFlags());
            Bundle args = destinationAction.getArguments();
            if (args != null) {
                intent.putExtras(args);
            }

            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, destinationAction.getRequestCode());
            } else {
                context.startActivity(intent);
            }

        } else {
            throw new IllegalStateException("Not support operate");
        }
    }


    private static void resolveDestinationDefinition(List<DestinationDefinition> destinationDefinitions) {
        if (isDestinationDefinitionResolved) {
            return;
        }


        int index = 0;
        for (DestinationDefinition destinationDefinition : destinationDefinitions) {
            if (destinationDefinition instanceof UriDestinationDefinition) {
                Uri uri = ((UriDestinationDefinition) destinationDefinition).getUri();

                String stringForUri = uri.toString();
                String path = uri.getPath();

                int pathIndex = stringForUri.indexOf(path);
                if (pathIndex != -1) {
                    path = stringForUri.substring(
                            pathIndex,
                            stringForUri.length()
                    );
                }

                matcher.addURI(uri.getAuthority(), path, index++);
            }
        }

        isDestinationDefinitionResolved = true;
    }


    protected abstract List<DestinationDefinition> getDestinationDefinitions();
}
