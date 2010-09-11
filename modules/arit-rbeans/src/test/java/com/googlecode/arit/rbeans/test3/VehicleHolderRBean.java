package com.googlecode.arit.rbeans.test3;

import com.googlecode.arit.rbeans.RBean;

@RBean(target="com.googlecode.arit.rbeans.test3.VehicleHolder")
public interface VehicleHolderRBean {
    VehicleRBean getVehicle();
}
