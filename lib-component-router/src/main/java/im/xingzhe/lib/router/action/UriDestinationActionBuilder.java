package im.xingzhe.lib.router.action;

public interface UriDestinationActionBuilder extends DestinationActionBuilder<UriDestinationActionBuilder> {

    UriDestinationActionBuilder authority(String authority);

    UriDestinationActionBuilder scheme(String scheme);

    UriDestinationActionBuilder path(String path);

    UriDestinationActionBuilder uriOnly(boolean uriOnly);
}
