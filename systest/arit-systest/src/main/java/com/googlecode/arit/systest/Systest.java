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
package com.googlecode.arit.systest;

import java.io.File;

public class Systest {
    public static void main(String[] args) throws Exception {
        File targetDir = new File("target").getAbsoluteFile();
        if (!targetDir.exists()) {
            // TODO
            throw new RuntimeException(targetDir + " doesn't exist");
        }
        File tmpDir = new File(targetDir, "systest-tmp");
        tmpDir.mkdir();
        System.out.println(new Application(Systest.class.getResource("arit-war.war"), tmpDir).getExplodedWAR());
    }
}
