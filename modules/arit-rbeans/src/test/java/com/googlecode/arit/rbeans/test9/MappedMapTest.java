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
package com.googlecode.arit.rbeans.test9;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.googlecode.arit.rbeans.RBeanFactory;

public class MappedMapTest {
    @Test
    public void testSize() throws Exception {
        RBeanFactory rbf = new RBeanFactory(MapHolderRBean.class);
        MapHolder mapHolder = new MapHolder();
        mapHolder.getMap().put(new Key("key"), new Value("value"));
        MapHolderRBean rbean = rbf.createRBean(MapHolderRBean.class, mapHolder);
        Map<KeyRBean,ValueRBean> map = rbean.getMap();
        assertEquals(1, map.size());
    }
    
    @Test
    public void testEntrySet() throws Exception {
        RBeanFactory rbf = new RBeanFactory(MapHolderRBean.class);
        MapHolder mapHolder = new MapHolder();
        mapHolder.getMap().put(new Key("key"), new Value("value"));
        MapHolderRBean rbean = rbf.createRBean(MapHolderRBean.class, mapHolder);
        Iterator<Map.Entry<KeyRBean,ValueRBean>> it = rbean.getMap().entrySet().iterator();
        assertTrue(it.hasNext());
        Map.Entry<KeyRBean,ValueRBean> entry = it.next();
        assertEquals("key", entry.getKey().getString());
        assertEquals("value", entry.getValue().getString());
    }
    
    @Test
    public void testValues() throws Exception {
        RBeanFactory rbf = new RBeanFactory(MapHolderRBean.class);
        MapHolder mapHolder = new MapHolder();
        mapHolder.getMap().put(new Key("key"), new Value("value"));
        MapHolderRBean rbean = rbf.createRBean(MapHolderRBean.class, mapHolder);
        Iterator<ValueRBean> it = rbean.getMap().values().iterator();
        assertTrue(it.hasNext());
        ValueRBean value = it.next();
        assertEquals("value", value.getString());
    }
}
