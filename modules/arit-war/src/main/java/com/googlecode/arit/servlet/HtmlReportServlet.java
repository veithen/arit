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
package com.googlecode.arit.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import com.googlecode.arit.ServerContext;
import com.googlecode.arit.report.ReportGenerator;

@Component(role=HttpServlet.class, hint="HtmlReportServlet")
public class HtmlReportServlet extends HttpServlet {
    @Requirement
    private ReportGenerator reportGenerator;
    
    private String version;
    
    private ServerContext getServerContext() {
        return new ServerContext(getServletContext(), getClass().getClassLoader());
    }
    
    @Override
    public void init() throws ServletException {
        try {
            InputStream in = HtmlReportServlet.class.getResourceAsStream("version.properties");
            try {
                Properties props = new Properties();
                props.load(in);
                version = props.getProperty("version");
                if (version.endsWith("-SNAPSHOT")) {
                    version = version + " (r" + props.getProperty("revision") + ")";
                }
            } finally {
                in.close();
            }
        } catch (IOException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!reportGenerator.isAvailable()) {
            request.setAttribute("serverContext", getServerContext());
            request.getRequestDispatcher("/WEB-INF/view/noprofile.jspx").forward(request, response);
        } else {
            request.setAttribute("version", version);
            // TODO: we should also display the unavailable ResourceEnumeratorFactory instances
            request.setAttribute("factories", reportGenerator.getAvailableResourceEnumeratorFactories());
            request.setAttribute("report", reportGenerator.generateReport());
            request.getRequestDispatcher("/WEB-INF/view/resources.jspx").forward(request, response);
        }
    }
}
