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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.arit.report.ClassLoaderLink;
import com.googlecode.arit.report.Identity;
import com.googlecode.arit.report.Module;
import com.googlecode.arit.report.Report;
import com.googlecode.arit.report.ReportGenerator;
import com.googlecode.arit.report.ResourcePresentation;
import com.googlecode.arit.report.xml.ClassLoaderLinkType;
import com.googlecode.arit.report.xml.IdentityType;
import com.googlecode.arit.report.xml.ModuleType;
import com.googlecode.arit.report.xml.ReportElement;
import com.googlecode.arit.report.xml.ResourceType;

public class XmlReportServlet extends HttpServlet {
    @Autowired
    private ReportGenerator reportGenerator;

    private JAXBContext jaxbContext;
    
    @Override
    public void init() throws ServletException {
        try {
            jaxbContext = JAXBContext.newInstance(ReportElement.class);
        } catch (JAXBException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Report report = reportGenerator.generateReport(false, true);
        ReportElement element = new ReportElement();
        element.setRootModules(convert(report.getRootModules()));
        response.setContentType("text/xml");
        Marshaller marshaller;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://arit.googlecode.com/report report.xsd");
            marshaller.marshal(element, response.getOutputStream());
        } catch (JAXBException ex) {
            throw new ServletException(ex);
        }
    }
    
    private List<ModuleType> convert(List<Module> modules) {
        List<ModuleType> result = new ArrayList<ModuleType>();
        for (Module module : modules) {
            result.add(convert(module));
        }
        return result;
    }
    
    private ModuleType convert(Module module) {
        ModuleType result = new ModuleType();
        result.setId(module.getId());
        result.setName(module.getName());
        result.setType(module.getType());
        result.setStopped(module.isStopped());
        result.setIcon("icons/module/" + module.getIcon());
        List<IdentityType> identities = new ArrayList<IdentityType>();
        for (Identity identity : module.getIdentities()) {
            identities.add(convert(identity));
        }
        result.setIdentities(identities);
        List<ResourceType> resources = new ArrayList<ResourceType>();
        for (ResourcePresentation resource : module.getResources()) {
            resources.add(convert(resource));
        }
        result.setResources(resources);
        result.setChildren(convert(module.getChildren()));
        return result;
    }
    
    private IdentityType convert(Identity identity) {
        IdentityType result = new IdentityType();
        result.setType(identity.getType());
        result.setValue(identity.getValue());
        return result;
    }
    
    private ResourceType convert(ResourcePresentation resource) {
        ResourceType result = new ResourceType();
        result.setId(resource.getId());
        result.setType(resource.getType());
        result.setDescription(resource.getDescription());
        List<ClassLoaderLinkType> links = new ArrayList<ClassLoaderLinkType>();
        for (ClassLoaderLink link : resource.getLinks()) {
            links.add(convert(link));
        }
        result.setLinks(links);
        return result;
    }
    
    private ClassLoaderLinkType convert(ClassLoaderLink link) {
        ClassLoaderLinkType result = new ClassLoaderLinkType();
        result.setDescription(link.getDescription());
        return result;
    }
}
