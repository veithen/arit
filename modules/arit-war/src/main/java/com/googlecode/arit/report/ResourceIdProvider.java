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
package com.googlecode.arit.report;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class ResourceIdProvider {
    private static class Entry {
        private final Map<Object,Integer> idMap = new WeakHashMap<Object,Integer>();
        private int nextId = 1;
        
        public synchronized Integer getId(Object object, boolean create) {
            Integer id = idMap.get(object);
            if (id == null && create) {
                id = nextId++;
                idMap.put(object, id);
            }
            return id;
        }
    }
    
    private final Map<String,Entry> entries = new HashMap<String,Entry>();
    
    public Integer getResourceId(String resourceTypeId, Object object, boolean create) {
        Entry entry;
        synchronized (entries) {
            entry = entries.get(resourceTypeId);
            if (entry == null) {
                entry = new Entry();
            }
            entries.put(resourceTypeId, entry);
        }
        return entry.getId(object, create);
    }
}
