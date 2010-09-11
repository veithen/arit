package com.googlecode.arit.rbeans.test6;

import com.googlecode.arit.rbeans.RBean;
import com.googlecode.arit.rbeans.SeeAlso;

@RBean(target="java.lang.Integer")
@SeeAlso(CyclicSeeAlsoRBean2.class)
public interface CyclicSeeAlsoRBean1 {

}
