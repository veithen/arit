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
package com.googlecode.arit.systest.tomcat;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;

import com.googlecode.arit.systest.Application;
import com.googlecode.arit.systest.Container;

public class TomcatContainer implements Container {
    private final Embedded container;
    private final Host host;
    
    public TomcatContainer(File workDir) {
        container = new Embedded();
        container.setCatalinaHome(new File(workDir, "catalina").getAbsolutePath());
        Engine engine = container.createEngine();
        engine.setName("engine");
        host = container.createHost("localhost", new File(workDir, "webapps").getAbsolutePath());
        engine.addChild(host);
        engine.setDefaultHost(host.getName());
        container.addEngine(engine);
        Connector httpConnector = container.createConnector("localhost", 8888, false);
        container.addConnector(httpConnector);
    }
    
    public String deployApplication(Application app) {
        Context context = container.createContext(app.getContextPath(), app.getExplodedWAR().getAbsolutePath());
        host.addChild(context);
        return app.getContextPath();
    }

    public void start() throws Exception {
        container.start();
    }

    public void stop() throws Exception {
        container.stop();
    }
}
