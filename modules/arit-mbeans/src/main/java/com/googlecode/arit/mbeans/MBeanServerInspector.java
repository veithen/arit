package com.googlecode.arit.mbeans;

import javax.management.MBeanServer;

public interface MBeanServerInspector {
    MBeanRepository inspect(MBeanServer mbs);
}
