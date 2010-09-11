package com.googlecode.arit.rbeans.test6;

import com.googlecode.arit.rbeans.RBean;
import com.googlecode.arit.rbeans.SeeAlso;

@RBean(target="java.lang.String")
@SeeAlso(CyclicSeeAlsoRBean1.class)
public interface CyclicSeeAlsoRBean2 {

}
