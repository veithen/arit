package com.googlecode.arit.mbeans;

import javax.management.ObjectName;

public interface MBeanRepository {
    Object retrieve(ObjectName name);
}
