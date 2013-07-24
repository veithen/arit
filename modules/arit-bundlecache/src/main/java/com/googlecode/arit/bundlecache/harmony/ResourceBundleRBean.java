/*
 * Copyright 2010,2013 Andreas Veithen
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
package com.googlecode.arit.bundlecache.harmony;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

import com.github.veithen.rbeans.Accessor;
import com.github.veithen.rbeans.StaticRBean;
import com.github.veithen.rbeans.TargetClass;

@TargetClass(ResourceBundle.class)
public interface ResourceBundleRBean extends StaticRBean {
    // In early IBM JRE 6.0 versions, the type of the cache attribute is
    // WeakHashMap<Object,Hashtable<String,ResourceBundle>>. In recent versions,
    // this was changed to WeakHashMap<ClassLoader,Hashtable<String,ResourceBundleSoftRef>>
    // where ResourceBundleSoftRef extends SoftReference (and stores a ResourceBundle)
    // reference.
    @Accessor(name="cache")
    WeakHashMap<?,Hashtable<String,?>> getCache();
}
