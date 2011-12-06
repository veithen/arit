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

import java.util.Dictionary;
import java.util.Enumeration;

import org.junit.Test;

import com.googlecode.arit.rbeans.RBeanFactory;

public class MappedDictionaryTest {
    @Test
    public void testSize() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DictionaryHolderRBean.class);
        DictionaryHolder dictionaryHolder = new DictionaryHolder();
        dictionaryHolder.getDictionary().put(new Key("key"), new Value("value"));
        DictionaryHolderRBean rbean = rbf.createRBean(DictionaryHolderRBean.class, dictionaryHolder);
        Dictionary<KeyRBean,ValueRBean> dictionary = rbean.getDictionary();
        assertEquals(1, dictionary.size());
    }
    
    @Test
    public void testKeys() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DictionaryHolderRBean.class);
        DictionaryHolder dictionaryHolder = new DictionaryHolder();
        dictionaryHolder.getDictionary().put(new Key("key"), new Value("value"));
        DictionaryHolderRBean rbean = rbf.createRBean(DictionaryHolderRBean.class, dictionaryHolder);
        Enumeration<KeyRBean> e = rbean.getDictionary().keys();
        assertTrue(e.hasMoreElements());
        KeyRBean value = e.nextElement();
        assertEquals("key", value.getString());
    }
    
    @Test
    public void testElements() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DictionaryHolderRBean.class);
        DictionaryHolder dictionaryHolder = new DictionaryHolder();
        dictionaryHolder.getDictionary().put(new Key("key"), new Value("value"));
        DictionaryHolderRBean rbean = rbf.createRBean(DictionaryHolderRBean.class, dictionaryHolder);
        Enumeration<ValueRBean> e = rbean.getDictionary().elements();
        assertTrue(e.hasMoreElements());
        ValueRBean value = e.nextElement();
        assertEquals("value", value.getString());
    }
    
    @Test
    public void testGet() throws Exception {
        RBeanFactory rbf = new RBeanFactory(DictionaryHolderRBean.class);
        DictionaryHolder dictionaryHolder = new DictionaryHolder();
        dictionaryHolder.getDictionary().put(new Key("key"), new Value("value"));
        DictionaryHolderRBean rbean = rbf.createRBean(DictionaryHolderRBean.class, dictionaryHolder);
        KeyRBean key = rbean.getDictionary().keys().nextElement();
        assertEquals("value", rbean.getDictionary().get(key).getString());
    }
}
