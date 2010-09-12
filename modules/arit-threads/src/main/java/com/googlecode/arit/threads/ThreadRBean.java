/*
 * Copyright 2010 Andreas Veithen
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
package com.googlecode.arit.threads;

import com.googlecode.arit.rbeans.Accessor;
import com.googlecode.arit.rbeans.RBean;

@RBean(target="java.lang.Thread")
public interface ThreadRBean {
    // "target" is used by Sun (1.5 and 1.6)
    // "runnable" is used by IBM
    @Accessor(name={"target", "runnable"})
    Runnable getTarget();
}
