package com.googlecode.arit.rbeans.test3;

import com.googlecode.arit.rbeans.RBean;
import com.googlecode.arit.rbeans.SeeAlso;

@RBean(target="com.googlecode.arit.rbeans.test3.Vehicle")
@SeeAlso({CarRBean.class, TruckRBean.class})
public interface VehicleRBean {

}
