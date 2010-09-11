package com.googlecode.arit.rbeans.test4;

import java.util.ArrayList;
import java.util.List;

public class StuffRegistry {
    private static final List<Stuff> registeredStuff = new ArrayList<Stuff>();
    
    public static void registerStuff(Stuff stuff) {
        registeredStuff.add(stuff);
    }
}
