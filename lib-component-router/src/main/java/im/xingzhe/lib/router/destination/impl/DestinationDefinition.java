package im.xingzhe.lib.router.destination.impl;

import java.util.List;

public class DestinationDefinition {

    private final String name;
    private final Class<?> destination;
    private final List<DestinationArgumentDefinition> inArgumentDefinitions;
    private final List<DestinationArgumentDefinition> outArgumentDefinitions;


    public DestinationDefinition(String name, Class<?> destination, List<DestinationArgumentDefinition> inArgumentDefinitions, List<DestinationArgumentDefinition> outArgumentDefinitions) {
        this.name = name;
        this.destination = destination;
        this.inArgumentDefinitions = inArgumentDefinitions;
        this.outArgumentDefinitions = outArgumentDefinitions;
    }

    public String getName() {
        return name;
    }

    public Class<?> getDestination() {
        return destination;
    }


    public List<DestinationArgumentDefinition> getInArgumentDefinitions() {
        return inArgumentDefinitions;
    }

    public List<DestinationArgumentDefinition> getOutArgumentDefinitions() {
        return outArgumentDefinitions;
    }
}
