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
package com.googlecode.arit.jdbc;

import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ResourceEnumerator;
import com.googlecode.arit.ResourceEnumeratorFactory;

@Component(role=ResourceEnumeratorFactory.class, hint="jdbc")
public class JdbcDriverEnumeratorFactory implements ResourceEnumeratorFactory {
    @Requirement(role=DriverManagerInspector.class)
    private List<DriverManagerInspector> inspectors;

    private DriverManagerInspector getInspector() {
        for (DriverManagerInspector inspector : inspectors) {
            if (inspector.isAvailable()) {
                return inspector;
            }
        }
        return null;
    }
    
    public boolean isAvailable() {
        return getInspector() != null;
    }

    public String getDescription() {
        return "JDBC drivers";
    }

    public ResourceEnumerator createEnumerator() {
        return new JdbcDriverEnumerator(getInspector().getDriverClasses());
    }
}
