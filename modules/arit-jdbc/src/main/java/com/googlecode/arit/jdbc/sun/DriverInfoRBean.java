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
package com.googlecode.arit.jdbc.sun;

import java.sql.Driver;

import com.github.veithen.rbeans.Accessor;
import com.github.veithen.rbeans.RBean;
import com.github.veithen.rbeans.Target;

@Target("java.sql.DriverInfo")
public interface DriverInfoRBean extends RBean {
    // The "driver" attribute exists in Java 1.5, 1.6 and 1.7. Java 1.5 and 1.6 also have
    // a "driverClass" attribute, but it's always set to driver.getClass().
    @Accessor(name="driver")
    Driver getDriver();
}
