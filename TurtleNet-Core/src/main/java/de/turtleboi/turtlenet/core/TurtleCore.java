package de.turtleboi.turtlenet.core;

import de.turtleboi.turtlenet.api.TurtleNet;
import de.turtleboi.turtlenet.core.util.ResourceUtil;

public class TurtleCore implements TurtleNet {
    public static final String VERSION = ResourceUtil.getProperty("version.properties", "version");
}
