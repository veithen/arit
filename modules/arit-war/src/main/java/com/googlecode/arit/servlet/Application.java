package com.googlecode.arit.servlet;

import java.util.ArrayList;
import java.util.List;

public class Application {
    private final String name;
    private final List<Resource> resources = new ArrayList<Resource>();
    
    public Application(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Resource> getResources() {
        return resources;
    }
}
