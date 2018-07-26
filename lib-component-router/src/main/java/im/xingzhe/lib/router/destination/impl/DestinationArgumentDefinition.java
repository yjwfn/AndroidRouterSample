package im.xingzhe.lib.router.destination.impl;

public class DestinationArgumentDefinition {

    private final String key;
    private final boolean require;
    private final Class<?> type;


    public DestinationArgumentDefinition(String key, boolean require, Class<?> type) {
        this.key = key;
        this.require = require;
        this.type = type;
    }

    public String getKey() {
        return key;
    }


    public boolean isRequire() {
        return require;
    }

    public Class<?> getType() {
        return type;
    }
}
