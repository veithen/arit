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
package com.googlecode.arit.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component(role=HttpServlet.class, hint="IconServlet")
public class IconServlet extends HttpServlet {
    @Requirement(role=IconManager.class)
    private Map<String,IconManager<?>> iconManagers;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        IconImage iconImage = null;
        if (pathInfo.charAt(0) == '/') {
            int idx = pathInfo.indexOf('/', 1);
            if (idx != -1) {
                IconManager<?> iconManager = iconManagers.get(pathInfo.substring(1, idx));
                if (iconManager != null) {
                    iconImage = iconManager.getByFileName(pathInfo.substring(idx+1));
                }
            }
        }
        if (iconImage == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            ImageData imageData = iconImage.getData();
            response.setContentType(imageData.getFormat().getContentType());
            response.getOutputStream().write(imageData.getContent());
        }
    }
}
