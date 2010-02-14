package com.google.code.rex.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.code.rex.ServerProfile;
import com.google.code.rex.jetty.JettyProfile;

public class InspectorServlet extends HttpServlet {
    private static final ServerProfile profile = new JettyProfile();
    
    private void visit(ThreadGroup threadGroup, PrintWriter out) {
        int numThreads = threadGroup.activeCount();
        Thread[] threads = new Thread[numThreads*2];
        numThreads = threadGroup.enumerate(threads, false);
        for (int i=0; i<numThreads; i++) {
            Thread thread = threads[i];
            ClassLoader tccl = thread.getContextClassLoader();
            out.print(threadGroup + " " + thread + " " + tccl);
            if (tccl != null) {
                out.print(" " + tccl.getClass().getName());
            }
            out.println();
            if (tccl != null) {
                out.println(profile.identifyApplication(tccl));
            }
        }

        int numGroups = threadGroup.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[numGroups*2];
        numGroups = threadGroup.enumerate(groups, false);
        for (int i=0; i<numGroups; i++) {
            visit(groups[i], out);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = tg.getParent()) != null) {
            tg = parent;
        }
        visit(tg, response.getWriter());
    }
}
