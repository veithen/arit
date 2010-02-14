package com.google.code.rex.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.rex.ResourceEnumerator;
import com.google.code.rex.ResourceEnumeratorFactory;
import com.google.code.rex.ServerContext;
import com.google.code.rex.ServerProfile;
import com.google.code.rex.ServerProfileFactory;

public class InspectorServlet extends HttpServlet {
    private ServerProfile profile;
    private List<ResourceEnumeratorFactory> resourceEnumeratorFactories;
    
    private ServerContext getServerContext() {
        return new ServerContext(getServletContext(), getClass().getClassLoader());
    }
    
    @Override
    public void init() throws ServletException {
        ServerContext serverContext = getServerContext();
        for (ServerProfileFactory spf : ProviderFinder.find(ServerProfileFactory.class)) {
            profile = spf.createServerProfile(serverContext);
            if (profile != null) {
                break;
            }
        }
        resourceEnumeratorFactories = ProviderFinder.find(ResourceEnumeratorFactory.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (profile == null) {
            request.setAttribute("serverContext", getServerContext());
            request.getRequestDispatcher("/WEB-INF/view/noprofile.jspx").forward(request, response);
        } else {
            List<Application> applications = new ArrayList<Application>();
            Map<ClassLoader,Application> classLoaderMap = new IdentityHashMap<ClassLoader,Application>();
            for (ResourceEnumeratorFactory resourceEnumeratorFactory : resourceEnumeratorFactories) {
                ResourceEnumerator resourceEnumerator = resourceEnumeratorFactory.createEnumerator();
                while (resourceEnumerator.next()) {
                    ClassLoader classLoader = resourceEnumerator.getClassLoader();
                    if (classLoader != null) {
                        Application application;
                        if (classLoaderMap.containsKey(classLoader)) {
                            application = classLoaderMap.get(classLoader);
                        } else {
                            String appName = profile.identifyApplication(classLoader);
                            if (appName == null) {
                                application = null;
                            } else {
                                application = new Application(appName);
                            }
                            classLoaderMap.put(classLoader, application);
                            applications.add(application);
                        }
                        if (application != null) {
                            application.getResources().add(new Resource(resourceEnumerator.getDescription()));
                        }
                    }
                }
            }
            request.setAttribute("applications", applications);
            request.getRequestDispatcher("/WEB-INF/view/resources.jspx").forward(request, response);
        }
    }
}
