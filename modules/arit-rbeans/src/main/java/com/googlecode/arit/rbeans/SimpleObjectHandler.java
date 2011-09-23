/*
 * Copyright 2010-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.arit.rbeans;

/**
 * {@link ObjectHandler} implementation that maps objects to corresponding RBeans.
 * 
 * @author Andreas Veithen
 */
public class SimpleObjectHandler extends ObjectHandler {
    private final RBeanFactory rbf;

    public SimpleObjectHandler(RBeanFactory rbf) {
        this.rbf = rbf;
    }

    protected Object doHandle(Object object) {
        RBeanInfo rbeanInfo = rbf.getRBeanInfoForTargetClass(object.getClass());
        return rbeanInfo == null ? object : rbf.createRBean(rbeanInfo, object);
    }
}
